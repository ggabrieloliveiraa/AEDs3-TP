<<<<<<< HEAD


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.DataInputStream;

import java.io.IOException;
import java.text.DecimalFormat;

import java.util.Scanner;
import java.io.RandomAccessFile;
import java.io.BufferedReader;

public class Main {
    
    public static void main(String[] args) throws NumberFormatException, IOException{

        BufferedReader objReader = new BufferedReader(new FileReader("../data/movies.csv"));

        //Scanner sc = new Scanner (System.in);
        String line = "";
        int control = 0; //controle para separar cada elemento
         //generate ids

        line = objReader.readLine();
        line = objReader.readLine();


        byte[] ba;
        int len;
        long pos0,pos1,pos2;

        //ler cada linha
        //while ((line = objReader.readLine()) != null) {
        while (control == 0) {
            int gameId = 0;
            String tmp = line;
            
            char x;

            //separando cada elemento do objeto    
            for (int j = 0; j < line.length(); j++) {
                if (tmp.charAt(j) == '\"'){
                    control++;
                }
                if ((control % 2) == 0 && tmp.charAt(j) == ',') {
                    tmp += "separatepls";
                } else {
                    x = tmp.charAt(j);
                    tmp += x;
                }
            }

            //prm = parametros do obj//
            String[] prm = new String[0];
            prm = tmp.split("separatepls", 0);
            System.out.println(prm[0]);

            //ordem que os elementos aparecem:
            //title,year,certificate,runtime,genre,rating,metascore,synopsis,director,votes,gross,cast1,cast2,cast3,cast4

                Movie m_temp= new Movie(gameId, prm[0], prm[1]);

                gameId++;


                try {

                    RandomAccessFile arq = new RandomAccessFile("../data/movies.bd", "rw");
        
                    pos0=arq.getFilePointer();
                    System.out.println("Registro iniciado na posição: "+pos0);
                    ba = m_temp.toByteArray();
                    arq.writeInt(ba.length); //Tamano do registro em bytes
                    arq.write(ba);
                    
                    //Lendo por ponteiro de trás para frente
                    //arq.seek(pos0);
                    //len = arq.readInt();
                    //ba = new byte[len];
                    //arq.read(ba);
                    //m_temp.fromByteArray(ba);
                    System.out.println(m_temp);
                          
        
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }  

/*
        Movie j1= new Movie(25, "Conceição", 49.90F);
        Movie j2= new Movie(37, "José Carlos", 62.50F);
        Movie j3= new Movie(291, "Pedro", 53.45F);
*/
        //Movie j_temp= new Movie();
    }
    


class Movie{
    
    protected int idMovie;
    protected String nome;
    protected String pontos;

    public Movie(int i, String n, String p){
        idMovie =  i;
        nome = n;
        pontos = p;  
    }
    
    public Movie(){
        idMovie = 0;
        nome = "";
        pontos = "";  
    }

    public String toString(){
        DecimalFormat df= new DecimalFormat("#,##0.00");
        return "\nID:"+idMovie +
                "\nNome:"+nome +
                "\nPontos:"+ pontos;
    }



    
    public byte[] toByteArray() throws IOException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idMovie);
        dos.writeUTF(nome);
        dos.writeUTF(pontos);

        return baos.toByteArray();
    }

    public void fromByteArray(byte ba[]) throws IOException{

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idMovie=dis.readInt();
        nome=dis.readUTF();
        pontos=dis.readUTF();

    }
}
=======
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
>>>>>>> refs/heads/gabriel
