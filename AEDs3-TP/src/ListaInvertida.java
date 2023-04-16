import java.io.RandomAccessFile;
import java.util.*;

public class ListaInvertida {
	private RandomAccessFile palavras; // arquivo invertido
	private RandomAccessFile indices; // lista invertida
	public static int tamLista = 5000; // 5000 bytes cabem 1248 registros(4 bytes pro tamanho e 4 pro ponteiro pra prox
										// lista)
	public static int maxRegistros = (tamLista - 8) / 4;

	ListaInvertida() throws Exception {
		// se nao existir o arquivo listainvertida ainda
		preencherCringe();
	}

	/*
	 * problema que pode ter: quando adicionar mais registros que corresponde a uma
	 * palavra específica, o tamanho pode aumentar e nao caber mais no arquivo com
	 * os indices soluções: tamanho fixo mt grande para cada lista invertida
	 * correspondente a uma palavra ou encadeamento(cada id tera um ponteiro para o
	 * proximo id)
	 */
	public void preencherCringe() throws Exception {
		this.palavras = new RandomAccessFile("../data/palavras.bin", "rw");
		this.indices = new RandomAccessFile("../data/indices.bin", "rw");
		CRUD crud = new CRUD("../data/arquivo.bin", 0);
		ArrayList<PalavraIndexada> palavrasIn = crud.getPalavrasIndexadas();
		palavras.seek(0);
		palavras.writeInt(palavrasIn.size());
		for (int i = 0; i < palavrasIn.size(); i++) {
			int freq = palavrasIn.get(i).id.size();
			palavras.writeUTF(palavrasIn.get(i).palavra); // escreve a palavra
			palavras.writeInt(freq); // escreve a frequencia
			System.out.println(palavrasIn.get(i).palavra);
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

	public void preencher() throws Exception { // nao consegui fazer funcionar mas se conseguir é poggers
		this.palavras = new RandomAccessFile("palavras.bin", "rw");
		this.indices = new RandomAccessFile("indices.bin", "rw");
		CRUD crud = new CRUD("arquivo.bin", 0);
		ArrayList<PalavraIndexada> palavrasIn = crud.getPalavrasIndexadas();
		for (int i = 0; i < palavrasIn.size(); i++) {
			for (int j = 0; j < palavrasIn.get(i).id.size(); j++) {
				inserir(palavrasIn.get(i).palavra, palavrasIn.get(i).id.get(j));
			}
		}
		crud.fechar();
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
			indices.writeInt((int) indices.length() + 4); // escreve ponteiro para nova lista que vai ser criada
			indices.seek(indices.length()); // vai pra nova lista
			indices.writeInt(1); // quantida de itens na lista
			indices.writeInt(id); // finalmente insere na lista o id
			System.out.println("????? uai = " + id);
		} else { // se a ultima lista ainda tiver espaço
			int novaQt = qtNaLista + 1;
			indices.seek(indices.getFilePointer() - 4);
			indices.writeInt(novaQt); // escreve nova quantidade de ids nessa lista
			indices.seek(indices.getFilePointer() + qtNaLista * 4); // vai pro final da lista
			System.out.println("salveeee, id = " + id);
			indices.writeInt(id); // escreve novo id		
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
					return;
				}
			}
		}
		PalavraArq pna = new PalavraArq(palavra, 1, (int) indices.length());
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

	public ArrayList<Integer> getIndices(PalavraArq pna) throws Exception {
		indices.seek(pna.apontadorIndice);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < pna.qtListas; i++) {
			if (i != pna.qtListas - 1) {
				int qtNaLista = indices.readInt();
				for (int j = 0; j < qtNaLista; j++) { // caso ainda nao esteja na ultima lista
					int id = indices.readInt();
					ids.add(id);
				}
				int nextPos = indices.readInt();
				indices.seek(nextPos); // vai para a proxima lista encadeada
			} else { // se ja estiver na ultima lista
				System.out.println("p = " + pna.palavra);
				int qtNaLista = indices.readInt();
				for (int j = 0; j < qtNaLista; j++) {
					int id = indices.readInt();
					ids.add(id);
				}
			}
		}
		return ids;
	}

	public PalavraArq getPalavra(String palavra) throws Exception {
		//busca a palavra e retorna suas propriedades(com a palavra, frequencia e apontador pro indice)
		palavras.seek(0);
		int qtPalavras = palavras.readInt();
		for (int i = 0; i < qtPalavras; i++) {
			PalavraArq pna = readPalavra();
			if (palavra.equals(pna.palavra)) {
				return (pna);
			}
		}
		return null;
	}

	public ArrayList<Integer> findEntry(String p) throws Exception {
		palavras.seek(0);
		PalavraArq palavra = getPalavra(p);
		ArrayList<Integer> entry = getIndices(palavra);
		return entry;
	}

	public ArrayList<Integer> buscar(String query) throws Exception {
		String[] words = query.split(" ");
		palavras.seek(0);
		indices.seek(0);
		ArrayList<Integer> result = findEntry(words[0]); // começa com a lista invertida da primeira palavra
		System.out.println("tr = " + result.size());
		for (int i = 0; i < result.size(); i++) {
			System.out.println("r = " + result.get(i));
			
		}
		for (int i = 1; i < words.length; i++) {
			ArrayList<Integer> entry = findEntry(words[i]);
			result.retainAll(entry); // interseção com a lista invertida da próxima palavra
		}
		for (int i = 0; i < result.size(); i++) {
			System.out.println("rd = " + result.get(i));
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