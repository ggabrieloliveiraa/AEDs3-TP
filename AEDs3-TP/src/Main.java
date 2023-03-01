import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class Main {
	public static void main(String[] args) {
		String csvFile = "/home/gabriel/git/AEDs3-TP/AEDs3-TP/src/movies.csv";
		String line = "";
		String cvsSplitBy = ",";
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			br.readLine(); // Ignora a primeira linha que geralmente contém cabeçalhos de coluna
			while ((line = br.readLine()) != null) {
				String[] atributos = line.split(cvsSplitBy);
				String title = atributos[0];
				Date year = null; // por enquanto(atributos[1])
				String certificate = atributos[2];
				int controle = 0;
				int numGen = 0;
				ArrayList<String> listaGen = new ArrayList<String>();
				String[] genre = { atributos[3] };
				if (atributos[3].charAt(0) == '\"') {
					System.out.println("!!!");
					for (int i = 3; atributos[i].charAt(atributos[i].length() - 1) != '\"'; i++) {
						System.out.println("!!!");
							listaGen.add(atributos[i]); // adicionara generos a lista enquanto não chegar no aspa
					}
					numGen = listaGen.size() + 1;
					listaGen.add(atributos[numGen + 2]);
					genre = new String[listaGen.size()];
					for (int i = 0; i < listaGen.size(); i++) {
						System.out.println(listaGen.get(i));
						genre[i] = listaGen.get(i);
					}
				}
				System.out.println(listaGen.get(0));
				float rating = Float.parseFloat(atributos[numGen + 2]);
				int metascore = Integer.parseInt(atributos[numGen + 2]);
				String director = atributos[6];
				System.out.println(genre.toString());
				// Crie um objeto usando as variáveis ​​lidas do arquivo
				//Movie filme = new Movie(title, year, certificate, genre, rating, metascore, director);
				// Faça algo com o objeto
				try (FileOutputStream fos = new FileOutputStream("./arquivo.bin")) {
					fos.write(filme.toByteArray());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String[] parseAttribute3(String generos) {
		if (generos.charAt(0) == '\"') {
			System.out.println(generos.charAt(generos.length() - 1));
			return generos.substring(1, generos.length() - 1).split(",");
		} else {
			System.out.println(generos.charAt(0));
			return new String[] { generos };
		}
	}
}