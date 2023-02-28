import java.io.RandomAccessFile;


public class Main {
    public static void main(String[] args){

        Movie j1= new Movie(25, "Conceição", 49.90F);
        Movie j2= new Movie(37, "José Carlos", 62.50F);
        Movie j3= new Movie(291, "Pedro", 53.45F);

        Movie j_temp= new Movie();

        byte[] ba;
        int len;
        long pos0,pos1,pos2;

        try {

            RandomAccessFile arq = new RandomAccessFile("../data/movies.csv", "rw");

            pos0=arq.getFilePointer();
            System.out.println("Registro iniciado na posição: "+pos0);
            ba = j1.toByteArray();
            arq.writeInt(ba.length); //Tamano do registro em bytes
            arq.write(ba);
            
            pos1=arq.getFilePointer();
            System.out.println("Registro iniciado na posição: "+pos1);
            ba = j2.toByteArray();
            arq.writeInt(ba.length); //Tamano do registro em bytes
            arq.write(ba);

            pos2=arq.getFilePointer();
            System.out.println("Registro iniciado na posição: "+pos2);
            ba = j3.toByteArray();
            arq.writeInt(ba.length); //Tamano do registro em bytes
            arq.write(ba);
            
            //Lendo por ponteiro de trás para frente
            arq.seek(pos2);
            len = arq.readInt();
            ba = new byte[len];
            arq.read(ba);
            j_temp.fromByteArray(ba);
            System.out.println(j_temp);

            arq.seek(pos1);
            len = arq.readInt();
            ba = new byte[len];
            arq.read(ba);
            j_temp.fromByteArray(ba);
            System.out.println(j_temp);

            arq.seek(pos0);
            len = arq.readInt();
            ba = new byte[len];
            arq.read(ba);
            j_temp.fromByteArray(ba);
            System.out.println(j_temp);

           
            
            

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    
}
