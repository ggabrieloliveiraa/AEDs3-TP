import java.io.IOException;
import java.io.RandomAccessFile;

public class CRUD {
	private RandomAccessFile file;

	public CRUD(String nomeArquivo) throws IOException {
		String tmp = "../data/" + nomeArquivo; 
		this.file = new RandomAccessFile(tmp, "rw");
	}

	public void fechar() throws IOException {
		file.close();
	}
	/*

	public void inserir(Movie movie) throws IOException {
		// Percorre o arquivo para encontrar um espaço livre grande o suficiente
		int posicao = 0;
		while (posicao < file.length()) {
			file.seek(posicao);

			int tamanho = file.readInt();
			byte lapide = file.readByte();

			if (lapide == false && tamanho >= movie.getTamanho()) {
				// Sobrescreve o movie removido com o novo movie
				file.seek(posicao);
				file.writeByte('0');
				file.writeInt(movie.getTamanho());
				file.writeInt(movie.getId());
				file.writeBytes(movie.getDados());
				return;
			}
			posicao += tamanho;
		}

		// Se não encontrar espaço livre, adiciona o movie ao final do arquivo
		file.seek(file.length());
		file.writeByte('0');
		file.writeInt(movie.getTamanho());
		file.writeInt(movie.getId());
		file.writeBytes(movie.getDados());
	}
*/
	public Movie buscar(int id) throws IOException {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = 0;
		int len;
		byte ba[];
		Movie j_temp = new Movie();
		while (posicao < file.length()) {
			file.seek(posicao);
			int tamanho = file.readInt();
			//System.out.println(posicao);
            //ba = new byte[tamanho];
            //file.read(ba);
            //file.seek(posicao + tamanho + 4);
            //j_temp.fromByteArray(ba);
            //System.out.println(j_temp);
			boolean lapide = file.readBoolean();
			file.seek(posicao + 4);
			int registroId = file.readInt();
			if (lapide == false && registroId == id) {
				file.seek(posicao + 4);
				ba = new byte[tamanho];
				file.read(ba);
				j_temp.fromByteArray(ba);
				return j_temp;
			}
			posicao += tamanho + 4;
		}
		return null;
	}
	public void atualizar(int id, String novosDados) throws IOException {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = 0;
		while (posicao < file.length()) {
			file.seek(posicao);
			byte lapide = file.readByte();
			int tamanho = file.readInt();
			int registroId = file.readInt();
			if (lapide == '0' && registroId == id) {
				// Sobrescreve os dados do movie
				file.seek(posicao + 1 + 4 + 4); //
				file.writeBytes(novosDados);
				return;
			}
			posicao += tamanho;
		}
	}



	public Movie remover(int id) throws IOException {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = apontar(id);
		Movie j_temp = new Movie();
		byte ba[];
		file.seek(posicao);
		int tamanho = file.readInt();

		ba = new byte[tamanho];
		file.read(ba);

		file.seek(posicao + 4);
		file.writeBoolean(true); //marca o movie como removido	
		
		j_temp.fromByteArray(ba);
		if (posicao != -1){
			System.out.println("ITEM REMOVIDO!");
		} else {
			System.out.println("ERRO: ITEM NÃO ENCONTRADO");
		}
		return j_temp;
	}
	/*
	 * int apontar - aponta pro inicio do registro
	 * @param int id - id do objeto procurado
	 * @return int - false = -1 true = posicao
	 */
	public int apontar(int id) throws IOException {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = 0;
		int len;
		while (posicao < file.length()) {
			file.seek(posicao);
			int tamanho = file.readInt();
			boolean lapide = file.readBoolean();
			file.seek(posicao + 4);
			int registroId = file.readInt();
			if (lapide == false && registroId == id) {
				return posicao;
			}
			posicao += tamanho + 4;
		}
		return -1;
	}

}