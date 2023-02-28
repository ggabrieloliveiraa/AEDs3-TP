import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {
        String csvFile = "movies.csv/./";
        String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Ignora a primeira linha que geralmente contém cabeçalhos de coluna
            while ((line = br.readLine()) != null) {
                String[] atributos = line.split(cvsSplitBy);
                String title = atributos[0];
                Date year = null; // por enquanto(atributos[1])
                String certificate = atributos[2];
                String[] genre = parseAttribute3(atributos[3]);
                float rating = Float.parseFloat(atributos[4]);
                int metascore = Integer.parseInt(atributos[5]);
                String director = atributos[6];
                // Crie um objeto usando as variáveis ​​lidas do arquivo
                Movie filme = new Movie(title, year, certificate, genre, rating, metascore, director);
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

    private static String[] parseAttribute3(String attribute3) {
        // Se o atributo 3 estiver entre aspas, remove as aspas e divide em um array
        if (attribute3.startsWith("\"") && attribute3.endsWith("\"")) {
            return attribute3.substring(1, attribute3.length() - 1).split(",");
        } else {
            // Caso contrário, retorna um array com apenas um elemento (o próprio valor do atributo)
            return new String[] { attribute3 };
        }
    }
}