import java.io.RandomAccessFile;
import java.util.*;

public class ListaInvertida {
	private RandomAccessFile palavras; // arquivo invertido
	private RandomAccessFile indices; // lista invertida
	private int tamInd = 5000;

	ListaInvertida() throws Exception {
		// se nao existir o arquivo listainvertida ainda
		preencher();
	}

	/*
	 * problema que pode ter: quando adicionar mais registros que corresponde a uma
	 * palavra específica, o tamanho pode aumentar e nao caber mais no arquivo com
	 * os indices soluções: tamanho fixo mt grande para cada lista invertida
	 * correspondente a uma palavra ou encadeamento(cada id tera um ponteiro para o
	 * proximo id)
	 */

	 public void preencher() throws Exception {
		this.palavras = new RandomAccessFile("../data/palavras.bin", "rw");
		this.indices = new RandomAccessFile("../data/indices.bin", "rw");
		CRUD crud = new CRUD("../data/arquivo.bin", 0);
		ArrayList<PalavraIndexada> palavrasIn = crud.getPalavrasIndexadas();
		palavras.seek(0);
		palavras.writeInt(palavrasIn.size()); // quantidade de palavras no dicionario no arquivo invertido
		for (int i = 0; i < palavrasIn.size(); i++) { // preenche o arquivo com as palavras
			palavras.writeUTF(palavrasIn.get(i).palavra);
			palavras.writeInt((int)indices.length());
			indices.seek(indices.length());
			indices.writeInt(palavrasIn.get(i).id.size()); // escreve o numero de ocorrencias da palavra

			int k =  (int)((palavrasIn.get(i).id.size()*4/(tamInd)) + 1);//
			if (k == 0) {
				k = 1;
			}

			for (int j = 0; j < k; j++){//quantidade de blocos de tamanho tamInd necessario
				
				if (j != k - 1){
					for (int h = 0; h < (tamInd - 4)/4; h++){
						indices.writeInt(palavrasIn.get(i).id.get(h*(j+1)));//escrever os indices daquela palavra
						if (h == ((tamInd - 4)/4) - 1){
							indices.writeInt((int)indices.length());
							indices.seek(indices.length());
						}
					}
				} else {
					for (int h = 0; h < (palavrasIn.get(i).id.size()) - ((tamInd/4)*(j)); h++) {
						indices.writeInt(palavrasIn.get(i).id.get(h*(j+1)));//escrever os indices daquela palavra
					}
				}
				
			}
		}
		crud.fechar();
	}

/*
	public void preencher() throws Exception {
		this.palavras = new RandomAccessFile("../data/palavras.bin", "rw");
		this.indices = new RandomAccessFile("../data/indices.bin", "rw");
		CRUD crud = new CRUD("../data/arquivo.bin", 0);
		ArrayList<PalavraIndexada> palavrasIn = crud.getPalavrasIndexadas();
		palavras.seek(0);
		palavras.writeInt(palavrasIn.size()); // quantidade de palavras no dicionario no arquivo invertido
		for (int i = 0; i < palavrasIn.size(); i++) { // preenche o arquivo com as palavras
			palavras.writeUTF(palavrasIn.get(i).palavra);
			System.out.println(palavrasIn.get(i).palavra);
			palavras.writeInt(i * 20000);
			indices.seek(i * 20000);
			indices.writeInt(palavrasIn.get(i).id.size()); // escreve o numero de ocorrencias da palavra
			for (int j = 0; j < palavrasIn.get(i).id.size(); j++) {
				// System.out.println("helton");
				// preenche a lista invertida com os ids respectivos das palavras
				indices.writeInt(palavrasIn.get(i).id.get(j));
				System.out.println(palavrasIn.get(i).id.get(j));
			}
		}
		crud.fechar();
	}
*/

	public ArrayList<Integer> getIndices() throws Exception {
		int t = indices.readInt();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		int iteracoes = (int)Math.ceil((t) / ((tamInd - 4)/4));//calculo de quantos buckets vao existir
		for (int j = 0; j < iteracoes; j++){
			if (j != iteracoes - 1){ //se estiver nos primeiros buckets
				for (int i = 0; i < (tamInd-4)/4; i++) {
					int id = indices.readInt();
					ids.add(id);
				}
				int prox = indices.readInt();//le o proximo bucket
				indices.seek(prox);
			} else {// se estiver no ultimo 'bucket'
				for (int i = 0; i < t - ((tamInd/4)*(j)); i++) {
					int id = indices.readInt();
					ids.add(id);
				}
			}
		}
		
		return ids;
	}

	public ArrayList<Integer> findEntry(String palavra) throws Exception {
		palavras.seek(0);
		int t = palavras.readInt();
		ArrayList<Integer> entry = new ArrayList<Integer>();
		for (int i = 0; i < t; i++) {
			String palavraNoArq = palavras.readUTF();
			int indPointer = palavras.readInt();
			System.out.println("pna = " + palavraNoArq);
			indices.seek(indPointer);
			int frequencia = indices.readInt();
			if (palavra.equals(palavraNoArq)) {
				int iteracoes = (int)(((frequencia) / ((tamInd - 4)/4))+1);//calculo de quantos buckets vao existir

				for (int j = 0; j < iteracoes; j++){
					if (j != iteracoes - 1){ //se estiver nos primeiros buckets
						for (int k = 0; k < (tamInd-4)/4; k++) {
							int id = indices.readInt();
							System.out.println("iiii = " + id);
							entry.add(id);
						}
					} else {// se estiver no ultimo 'bucket'
						for (int k = 0; k < frequencia - ((tamInd/4)*(j)); k++) {
							System.out.println("else");
								int id = indices.readInt();
								System.out.println("iiii = " + id);
								entry.add(id);
						}
					}
				}
			}
		}
		return entry;
	}

	public int apontar (String palavra, int id) throws Exception {
		palavras.seek(0);
		int tam = palavras.readInt();
		int pos = -1;
		String tmp;
		for (int i = 0; i < tam; i++){
			tmp = palavras.readUTF();
			pos = palavras.readInt();
			if (tmp.equals(palavra)){
				i = tam;
			}
		}
		indices.seek(pos);
		int frequencia = indices.readInt();

		int iteracoes = (int)(((frequencia) / ((tamInd - 4)/4))+1);//calculo de quantos buckets vao existir

		for (int j = 0; j < iteracoes; j++){
			if (j != iteracoes - 1){ //se estiver nos primeiros buckets
				for (int k = 0; k < (tamInd-4)/4; k++) {
					int idi = indices.readInt();
					if (idi == id){
						return ((int)(indices.getFilePointer() - 4));
					}
				}
			} else {// se estiver no ultimo 'bucket'
				for (int k = 0; k < frequencia - ((tamInd/4)*(j)); k++) {
					System.out.println("else");
						int idi = indices.readInt();
						if (idi == id){
							return ((int)(indices.getFilePointer() - 4));
						}
				}
			}
		}
		
		return (-1);

	}

	public boolean remover (String palavra, int id) throws Exception {
		int posRemov = apontar (palavra, id);
		if (posRemov != -1){
			indices.seek(posRemov);
			indices.writeInt(-1);
			System.out.println("ITEM REMOVIDO!");
			return (true);
		} else {
			System.out.println("ERRO: ITEM NÃO ENCONTRADO");
			return false;
		}

	}

	public ArrayList<Integer> buscar(String query) throws Exception {
		String[] words = query.split(" ");
		palavras.seek(0);
		indices.seek(0);
		ArrayList<Integer> result = findEntry(words[0]); // começa com a lista invertida da primeira palavra
		// for (int i = 0; i < 10065; i++) {
		// result.add(i);
		// }
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