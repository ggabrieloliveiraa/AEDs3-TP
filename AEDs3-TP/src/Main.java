

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
        int gameId = 0; //generate ids

        line = objReader.readLine();
        System.out.println(line);
        line = objReader.readLine();
        System.out.println(line);

        byte[] ba;
        int len;
        long pos0,pos1,pos2;

        //ler cada linha
        //while ((line = objReader.readLine()) != null) {
        while (control == 0) {
            String tmp = line;
            
            char x;

            //separando cada elemento do objeto    
            for (int j = 0; j < tmp.length(); j++) {
                if (tmp.charAt(j) == '\"'){
                    control++;
                }
                if ((control % 2) == 0 && tmp.charAt(j) == ',') {
                    tmp += "separatepls";
                } else {
                    x = tmp.charAt(j);
                    tmp += x;
                }
                System.out.println(tmp);
            }

            System.out.println(tmp);
            //prm = parametros do obj
            String[] prm = new String[0];
            prm = tmp.split("separatepls", 0);

            //ordem que os elementos aparecem:
            //title,year,certificate,runtime,genre,rating,metascore,synopsis,director,votes,gross,cast1,cast2,cast3,cast4

                Movie m_temp= new Movie(prm[0], prm[1], prm[2]);

                


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
    
    protected String idMovie;
    protected String nome;
    protected String pontos;

    public Movie(String i, String n, String p){
        idMovie =  i;
        nome = n;
        pontos = p;  
    }
    
    public Movie(){
        idMovie = "";
        nome = "";
        pontos = "";  
    }

    public String toString(){
        DecimalFormat df= new DecimalFormat("#,##0.00");
        return "\nID:"+idMovie +
                "\nNome:"+nome +
                "\nPontos:"+ df.format(pontos);
    }



    
    public byte[] toByteArray() throws IOException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(idMovie);
        dos.writeUTF(nome);
        dos.writeUTF(pontos);

        return baos.toByteArray();
    }

    public void fromByteArray(byte ba[]) throws IOException{

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idMovie=dis.readUTF();
        nome=dis.readUTF();
        pontos=dis.readUTF();

    }
}
