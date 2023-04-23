import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class ListaInvertida {
	private int tipo; // tipo 0 = gen; tipo 1 = dir;
	private RandomAccessFile palavras; // arquivo invertido
	private RandomAccessFile indices; // lista invertida
	public static int tamLista = 5000; // 5000 bytes cabem 1248 registros(4 bytes pro tamanho e 4 pro ponteiro pra prox
										// lista)
	public static int maxRegistros = (tamLista - 8) / 4; // quantos registros cabe em uma lista

	ListaInvertida(boolean isCargaInicial, int tipo) throws Exception {
		this.tipo = tipo;
		abrirArquivo();
		if (palavras.length() == 0) {// se nao existir o arquivo listainvertida ainda
			preencher();
			System.out.println("Lista invertida criada!");
		} else if (isCargaInicial) {
			deletarArquivo();
			abrirArquivo();
		}
	}

	public void deletarArquivo() throws Exception {
		if (this.tipo == 0) {
			File p = new File("../data/palavrasGen.bin");
			File i = new File("../data/indicesGen.bin");
			p.delete();
			i.delete();
			abrirArquivo();
		} else if (this.tipo == 1) {
			File p = new File("../data/palavrasDir.bin");
			File i = new File("../data/indicesDir.bin");
			p.delete();
			i.delete();
		}
	}

	public void abrirArquivo() throws Exception {
		if (this.tipo == 0) {
			this.palavras = new RandomAccessFile("../data/palavrasGen.bin", "rw");
			this.indices = new RandomAccessFile("../data/indicesGen.bin", "rw");
		} else if (this.tipo == 1) {
			this.palavras = new RandomAccessFile("../data/palavrasDir.bin", "rw");
			this.indices = new RandomAccessFile("../data/indicesDir.bin", "rw");
		}
	}

	public void preencher() throws Exception {//preencherCringe F 
		ArrayList<PalavraIndexada> palavrasIn;
		abrirArquivo();
		CRUD crud = new CRUD("../data/arquivo.bin", 0);
		if (tipo == 0) {
			palavrasIn = crud.getPalavrasIndexadas();
		} else {
			palavrasIn = crud.getPalavrasIndexadasDir();
		}
		palavras.seek(0);
		palavras.writeInt(palavrasIn.size());
		for (int i = 0; i < palavrasIn.size(); i++) {
			int freq = palavrasIn.get(i).id.size();
			palavras.writeUTF(palavrasIn.get(i).palavra); // escreve a palavra
			palavras.writeInt(freq); // escreve a frequencia
			palavras.writeInt((int) indices.length()); // escreve o ponteiro pros indices
			indices.seek((int) indices.length());
			preencherIndices(palavrasIn.get(i).id);
		}
		crud.fechar();
	}

	public void preencherIndices(ArrayList<Integer> ids) throws Exception { // preenche os arquivos de indice
		int freq = ids.size();
		int qtLista = 1 + (freq / maxRegistros);
		int numId = 0;
		for (int i = 0; i < qtLista; i++) {
			if (i != qtLista - 1) { // se nao estiver na ultima lista ainda(sera uma lista cheia)
				indices.writeInt(maxRegistros);
				for (int j = 0; j < maxRegistros; j++) {
					indices.writeInt(ids.get(numId));
					numId++;
				}
				indices.writeInt((int) indices.length() + 4); // escreve o ponteiro para a proxima lista
				indices.seek(indices.length());
			} else { // se estiver na ultima lista a ser inserida
				indices.writeInt(freq - maxRegistros * i);
				for (int k = 0; k < freq - maxRegistros * i; k++) {
					indices.writeInt(ids.get(numId));
					numId++;
				}
			}
		}
	}

	public void goToNextLista() throws Exception { // pula para a proxima lista(vai ate o final e le o ponteiro)
		indices.seek(indices.getFilePointer() + tamLista - 4);
		int nextLista = indices.readInt();
		indices.seek(nextLista);
	}

	public void inserirNaLista(int id) throws Exception {
		int qtNaLista = indices.readInt();
		if (qtNaLista == maxRegistros) { // se a ultima lista estiver cheia, cria nova lista
			indices.seek(indices.getFilePointer() + 4 * qtNaLista); // vai pro final da lista
			int a = (int) indices.length() % tamLista;
			int qtFalta = tamLista - a; // quanto falta pra proxima lista
			if (a == 0) {
				qtFalta = 0;
			}
			indices.writeInt((int) indices.length() + qtFalta); // escreve ponteiro para nova lista que vai ser criada
			indices.seek(indices.length() + qtFalta); // vai pra nova lista
			indices.writeInt(1); // quantida de itens na lista
			indices.writeInt(id); // finalmente insere na lista o id
		} else { // se a ultima lista ainda tiver espaço
			int novaQt = qtNaLista + 1;
			indices.seek(indices.getFilePointer() - 4);
			indices.writeInt(novaQt); // escreve nova quantidade de ids nessa lista
			indices.seek(indices.getFilePointer() + qtNaLista * 4); // vai pro final da lista
			indices.writeInt(id); // escreve novo id
			indices.seek(indices.getFilePointer() - 8);
		}
	}

	public void inserirIndice(int id, PalavraArq pna) throws Exception {
		indices.seek(pna.apontadorIndice);
		int numListas = pna.qtListas;
		for (int i = 0; i < numListas; i++) {
			if (i != numListas - 1) { // se nao estiver na ultima lista(a que vai inserir ainda)
				goToNextLista();
			} else { // se estiver na ultima lista, na que vai inserir
				inserirNaLista(id);
			}
		}

	}

	public PalavraArq readPalavra() throws Exception {
		String word = palavras.readUTF();
		int freq = palavras.readInt();
		int pos = palavras.readInt();
		PalavraArq palavra = new PalavraArq(word, freq, pos);
		return palavra;
	}

	public void escrevePalavra(PalavraArq p) throws Exception {
		if (palavras.length() == 0) {
			palavras.seek(4);
		}
		palavras.writeUTF(p.palavra);
		palavras.writeInt(p.freq);
		palavras.writeInt(p.apontadorIndice);
		palavras.seek(0);
		int qtPalavras = palavras.readInt() + 1;
		palavras.seek(0);
		palavras.writeInt(qtPalavras); // atualiza quantidade de palavras
	}

	public void inserir(String palavra, int id) throws Exception {
		if (palavras.length() != 0) {
			palavras.seek(0);
			int qtPalavras = palavras.readInt();
			for (int i = 0; i < qtPalavras; i++) {
				PalavraArq pna = readPalavra();
				if (palavra.equals(pna.palavra)) {
					inserirIndice(id, pna);
					palavras.seek(palavras.getFilePointer() - 8);
					int newFreq = pna.freq + 1;
					palavras.writeInt(newFreq);
					return;
				}
			}
		}
		int a = (int) indices.length() % tamLista;
		int qtFalta = tamLista - a; // quanto falta pra proxima lista
		if (a == 0) {
			qtFalta = 0;
		}
		PalavraArq pna = new PalavraArq(palavra, 1, (int) indices.length() + qtFalta);
		escrevePalavra(pna);
		inserirIndice(id, pna);
	}

	public ArrayList<Integer> readLista() throws Exception {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		int qtNaLista = indices.readInt();
		for (int i = 0; i < qtNaLista; i++) {
			int id = indices.readInt();
			ids.add(id);
		}
		return ids;
	}

	public void removerId(int idProcurado, PalavraArq pna) throws Exception {
		indices.seek(pna.apontadorIndice);
		for (int i = 0; i < pna.qtListas; i++) {
			if (i != pna.qtListas - 1) {
				int qtNaLista = indices.readInt();
				for (int j = 0; j < qtNaLista; j++) { // caso ainda nao esteja na ultima lista
					int id = indices.readInt();
					if (id == idProcurado) {
						indices.seek(indices.getFilePointer() - 4);
						indices.writeInt(-1);
					}
				}
				int nextPos = indices.readInt();
				indices.seek(nextPos); // vai para a proxima lista encadeada
			} else { // se ja estiver na ultima lista
				int qtNaLista = indices.readInt();
				for (int j = 0; j < qtNaLista; j++) {
					int id = indices.readInt();
					if (id == idProcurado) {
						indices.seek(indices.getFilePointer() - 4);
						indices.writeInt(-1);
					}
				}
			}
		}
	}

	public void remover(String palavra, int id) throws Exception {
		PalavraArq pna = getPalavra(palavra);
		removerId(id, pna);
	}

	public ArrayList<Integer> getIndices(PalavraArq pna) throws Exception {
		indices.seek(pna.apontadorIndice);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < pna.qtListas; i++) {
			if (i != pna.qtListas - 1) {
				int qtNaLista = indices.readInt();
				for (int j = 0; j < qtNaLista; j++) { // caso ainda nao esteja na ultima lista
					int id = indices.readInt();
					if (id != -1) {
						ids.add(id);
					}
				}
				int nextPos = indices.readInt();
				indices.seek(nextPos); // vai para a proxima lista encadeada
			} else { // se ja estiver na ultima lista
				int qtNaLista = indices.readInt();
				for (int j = 0; j < qtNaLista; j++) {
					int id = indices.readInt();
					if (id != -1) {
						ids.add(id);
					}
				}
			}
		}
		return ids;
	}

	public PalavraArq getPalavra(String palavra) throws Exception {
		// busca a palavra e retorna suas propriedades(com a palavra, frequencia e
		// apontador pro indice)
		palavras.seek(0);
		int qtPalavras = palavras.readInt();
		for (int i = 0; i < qtPalavras; i++) {
			PalavraArq pna = readPalavra();
			if (palavra.equalsIgnoreCase(pna.palavra)) {
				return (pna);
			}
		}
		return null;
	}

	public ArrayList<Integer> findEntry(String p) throws Exception {
		palavras.seek(0);
		PalavraArq palavra = getPalavra(p);
		ArrayList<Integer> entry = new ArrayList<Integer>();
		if (palavra != null) {
			entry = getIndices(palavra);
		}
		return entry;
	}

	public ArrayList<Integer> buscar(String query) throws Exception {
		String[] words = query.split(" ");
		palavras.seek(0);
		indices.seek(0);
		ArrayList<Integer> result = findEntry(words[0]); // começa com a lista invertida da primeira palavra
		for (int i = 1; i < words.length; i++) {
			ArrayList<Integer> entry = findEntry(words[i]);
			result.retainAll(entry); // interseção com a lista invertida da próxima palavra
		}
		return result;
	}
}

class PalavraArq {
	public String palavra;
	public int freq;
	public int apontadorIndice;
	public int qtListas;

	PalavraArq(String pal, int f, int i) {
		this.palavra = pal;
		this.freq = f;
		this.apontadorIndice = i;
		this.qtListas = 1 + (this.freq / ((ListaInvertida.tamLista - 8) / 4));
	}
}

class PalavraIndexada {
	public String palavra;
	public ArrayList<Integer> id;

	PalavraIndexada(String p) {
		this.palavra = p;
		this.id = new ArrayList<Integer>();
	}

	public void addId(int id) {
		this.id.add(id);
	}

}