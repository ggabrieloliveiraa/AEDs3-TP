import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main2 {

	public static void main(String[] args) {
		List<Movie> filmes = readCsv("/home/gabriel/git/AEDs3-TP/AEDs3-TP/src/movies.csv");
		byte ba[];
		int len;
		Movie j_temp= new Movie();
		try {

			RandomAccessFile fos = new RandomAccessFile("arquivo.bin", "rw");

			for (int i = 0; i < filmes.size(); i++) {
				System.out.println("Posicao do registro: " + fos.getFilePointer());
				ba = filmes.get(i).toByteArray();
	            fos.writeInt(ba.length); //tamanho do registro em bytes
	            fos.write(ba); //vetor de bytes que descrevem o objeto
			}
	        fos.seek(860378);
	        len = fos.readInt();
	        ba = new byte[len]; 
	        fos.read(ba);
	        j_temp.fromByteArray(ba);
	        System.out.println(j_temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//readBinary();
	}

	public static List<Movie> readCsv(String filename) {
		List<Movie> filmes = new ArrayList<>();
		int id = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			br.readLine(); // Ignora a primeira linha que contém cabeçalhos de coluna
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String[] atributos = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				// o regex acima divide a linha em campos, ignorando as vírgulas entre aspas
				String title = atributos[0];
				Date year = new Date();// por enquanto deixei assim(atributos[1])
				String certificate = atributos[2];
				String[] genre = atributos[3].replaceAll("^\"|\"$", "").split(",");
				float rating = Float.parseFloat(atributos[4]);
				String director = atributos[5];
				Movie filme = new Movie(false, id, title, year, certificate, genre, rating, director);
				id++;
				filmes.add(filme);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filmes;
	}

	public static void readBinary() {
		File file = new File("/home/gabriel/git/AEDs3-TP/AEDs3-TP/arquivo.bin");
																					

		try {
			FileInputStream inputStream = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()]; // cria um array de bytes com o tamanho do arquivo
			int bytesRead = inputStream.read(bytes); // lê o arquivo para o array de bytes

			// Imprime o array de bytes em formato hexadecimal
			for (int i = 0; i < bytesRead; i++) {
				System.out.printf("%02X ", bytes[i]);
			}

			inputStream.close(); // fecha o arquivo
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}