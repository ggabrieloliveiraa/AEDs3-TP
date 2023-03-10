import java.io.*;
import java.util.Arrays;

public class OrdenacaoExterna {
	private static void quickSort(Movie[] vetor, int inicio, int fim) {
		if (inicio < fim) {
			int posicaoPivo = separar(vetor, inicio, fim);
			quickSort(vetor, inicio, posicaoPivo - 1);
			quickSort(vetor, posicaoPivo + 1, fim);
		}
	}

	private static int separar(Movie[] vetor, int inicio, int fim) {
		Movie pivo = vetor[inicio];
		int i = inicio + 1, f = fim;
		while (i <= f) {
			if (vetor[i].id <= pivo.id)
				i++;
			else if (pivo.id < vetor[f].id)
				f--;
			else {
				Movie troca = vetor[i];
				vetor[i] = vetor[f];
				vetor[f] = troca;
				i++;
				f--;
			}
		}
		vetor[inicio] = vetor[f];
		vetor[f] = pivo;
		return f;
	}

	public static void externalSort(String filename, int m, int n) throws IOException {

		String[] arqs = new String[n];
		byte ba[];
		RandomAccessFile input = new RandomAccessFile(filename + ".bin", "rw");
		for (int i = 0; i < n; i++) { //cria os n arquivos
			RandomAccessFile output = new RandomAccessFile(filename + (i + 1) + "tmp.bin", "rw");
			arqs[i] = filename + (i+1) + "tmp.bin"; 
			output.close();
		}
		int controle = 1;
		input.seek(4); //pula o cabeçalho que contem o maxId
		while (input.getFilePointer() < input.length()){ //rodar o arquivo binario inteiro
			System.out.println("!!!!!!!!!");
			int tamanhos[] = new int[m];
			Movie[] filmes = new Movie[m];
			for (int i = 0; i < m; i++) {
				filmes[i] = new Movie();
			}
			for (int i = 0; i < m; i++) {
				if (input.getFilePointer() < input.length())
				{
				//System.out.println(input.getFilePointer());
				tamanhos[i] = input.readInt();
				//System.out.println("t = " + tamanhos[i] + "  / i = " + i);
				ba = new byte[tamanhos[i]];
				input.read(ba);
				filmes[i].fromByteArray(ba);
				}
			}
			quickSort(filmes, 0, filmes.length - 1); //ordena os blocos de tamanho m atributos em memoria principal
			if (controle%n == 0)
			{
				distribuir(tamanhos, filmes, filename, n);
			}
			else{
			distribuir(tamanhos, filmes, filename, controle % n); //distribui os arquivos nos diferentes caminhos(arquivos)
			                                                      //n%controle(n numero max)
			}
			controle++;

		}
		//intercalacaoBalanceada(arqs, m);
		input.close();
	}

	private static int calcularPassadas(int m, int n) throws IOException {
		CRUD crud = new CRUD("/home/gabriel/git/AEDs3-TP/AEDs3-TP/arquivo.bin");
		int maxId = crud.getMaxId();
		crud.fechar();
		int passadas = (int) (1 + Math.ceil((Math.log((maxId / m))) / Math.log(n)));
		return passadas;

	}

	private static void intercalacaoBalanceada(String[] inputFiles, int m) throws IOException {
		int n = inputFiles.length;
		byte[] ba;
		int[] tamanhos = new int[n];
		Movie[] j_temp = new Movie[n];
		int passadas = calcularPassadas(m, n);
		for (int i = 0; i < passadas; i++) {
			for (int j = 0; j < m * n; j++) {
				for (int k = 0; k < n; k++) { 
					RandomAccessFile raf1 = new RandomAccessFile(inputFiles[k], "r");
					tamanhos[k] = raf1.readInt();
					ba = new byte[tamanhos[k]];
					j_temp[k].fromByteArray(ba);
				}
			}
		}
	}

	public static void sort(String inputFiles1, String inputFiles2) throws IOException {
		byte ba1[];
		byte ba2[];
		int num = inputFiles2.charAt(inputFiles2.length());
		// for (int i = 0; i < inputFiles.length; i++) {
		RandomAccessFile raf1 = new RandomAccessFile(inputFiles1, "r");
		RandomAccessFile raf2 = new RandomAccessFile(inputFiles2, "r");
		RandomAccessFile rafOut = new RandomAccessFile(inputFiles2.substring(0, inputFiles2.length() - 1) + num, "rw");
		Movie j_temp1 = new Movie();
		Movie j_temp2 = new Movie();
		int tamanho1 = raf1.readInt();
		int tamanho2 = raf2.readInt();
		ba1 = new byte[tamanho1];
		ba2 = new byte[tamanho2];
		j_temp1.fromByteArray(ba1);
		j_temp2.fromByteArray(ba2);
		if(j_temp1.id < j_temp2.id) {
			rafOut.writeInt(ba1.length);
			rafOut.write(ba1);
		}
		else {
			rafOut.writeInt(ba2.length);
			rafOut.write(ba2);
		}
		raf1.close();
		raf2.close();
		rafOut.close();
		// RandomAccessFile raf1 = new RandomAccessFile(inputFile1, "r");
		// RandomAccessFile raf2 = new RandomAccessFile(inputFile2, "r");
		// RandomAccessFile rafOut = new RandomAccessFile(outputFile, "rw");

	}

	/*
	 * String outputFile = "output.bin"; // arquivo de saída
	 * 
	 * int numFiles = inputFiles.length;
	 * 
	 * // iterar até que haja apenas um arquivo while (numFiles > 1) { int k = (int)
	 * Math.ceil((double) numFiles / 2);
	 * 
	 * for (int i = 0; i < k; i++) { int j = i + k; if (j < numFiles) {
	 * mergeFiles(inputFiles[i], inputFiles[j], "temp" + i + ".bin"); } else { File
	 * file = new File(inputFiles[i]); file.renameTo(new File("temp" + i + ".bin"));
	 * } }
	 * 
	 * for (int i = 0; i < numFiles; i++) { File file = new File(inputFiles[i]);
	 * file.delete(); }
	 * 
	 * for (int i = 0; i < k; i++) { File file = new File("temp" + i + ".bin");
	 * file.renameTo(new File(inputFiles[i])); }
	 * 
	 * numFiles = k; }
	 * 
	 * // renomear arquivo final File finalFile = new File(inputFiles[0]);
	 * finalFile.renameTo(new File(outputFile));
	 */

	private static void mergeFiles(String inputFile1, String inputFile2, String outputFile) throws IOException {
		RandomAccessFile raf1 = new RandomAccessFile(inputFile1, "r");
		RandomAccessFile raf2 = new RandomAccessFile(inputFile2, "r");
		RandomAccessFile rafOut = new RandomAccessFile(outputFile, "rw");

		byte[] buffer1 = new byte[4096]; // buffer size
		byte[] buffer2 = new byte[4096]; // buffer size

		int bytesRead1 = raf1.read(buffer1);
		int bytesRead2 = raf2.read(buffer2);

		while (bytesRead1 > 0 && bytesRead2 > 0) {
			if (buffer1[0] < buffer2[0]) {
				rafOut.write(buffer1, 0, bytesRead1);
				bytesRead1 = raf1.read(buffer1);
			} else {
				rafOut.write(buffer2, 0, bytesRead2);
				bytesRead2 = raf2.read(buffer2);
			}
			bytesRead1 = raf1.read(buffer1);
		}

		while (bytesRead1 > 0) {
			rafOut.write(buffer1, 0, bytesRead1);
			bytesRead1 = raf1.read(buffer1);
		}

		while (bytesRead2 > 0) {
			rafOut.write(buffer2, 0, bytesRead2);
			bytesRead2 = raf2.read(buffer2);
		}

		raf1.close();
		raf2.close();
		rafOut.close();
	}

	private static void distribuir(int[] tamanhos, Movie[] filmes, String filename, int n) throws IOException {
		RandomAccessFile output = new RandomAccessFile(filename + n + "tmp.bin", "rw");
		byte ba[];
		for (int i = 0; i < filmes.length; i++) {
			ba = new byte[tamanhos[i]];
			ba = filmes[i].toByteArray();
			output.writeInt(tamanhos[i]);
			output.write(ba);
		}
		output.close();
	}
}
