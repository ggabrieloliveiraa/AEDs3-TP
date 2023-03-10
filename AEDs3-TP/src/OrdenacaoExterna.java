import java.io.*;
import java.util.Arrays;
import java.util.*;

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
		CRUD crud = new CRUD("../data/arquivo.bin");
		int max = crud.getMaxId();
		String[] arqs = new String[n];
		byte ba[];
		RandomAccessFile input = new RandomAccessFile(filename + ".bin", "rw");
		for (int i = 0; i < n; i++) { // cria os n arquivos
			RandomAccessFile output = new RandomAccessFile(filename + (i + 1) + "tmp.bin", "rw");
			arqs[i] = filename + (i + 1) + "tmp.bin";
			output.close();
		}
		int controle = 1;
		input.seek(4); // pula o cabeçalho que contem o maxId
		while (input.getFilePointer() < input.length()) { // rodar o arquivo binario inteiro
			//System.out.println("????????????????");
			int tamanhos[] = new int[m];
			Movie[] filmes = new Movie[m];
			for (int i = 0; i < m; i++) {
				filmes[i] = new Movie();
			}
			for (int i = 0; i < m; i++) {
				if (input.getFilePointer() < input.length()-1) {
					tamanhos[i] = input.readInt();
					ba = new byte[tamanhos[i]];
					input.read(ba);
					filmes[i].fromByteArray(ba);
				}
			}
			quickSort(filmes, 0, filmes.length - 1); // ordena os blocos de tamanho m atributos em memoria principal
			if (controle % n == 0) {
				//System.out.println("abc!");
				distribuir(tamanhos, filmes, filename, n);
			} else {
				//System.out.println("abcde!?");
				distribuir(tamanhos, filmes, filename, controle % n); // distribui os arquivos nos diferentes
																		// caminhos(arquivos)
																		// n%controle(n numero max)
			}
			controle++;

		}
		//System.out.println("AQUIIIIII");
		//crud.mostrarTudo("arquivo1tmp.bin", 0);
		intercalacaoBalanceada(arqs, m);
		input.close();
	}

	private static int calcularPassadas(int m, int n) throws IOException {
		CRUD crud = new CRUD("../data/arquivo.bin");
		double maxId = crud.getMaxId();
		crud.fechar();
		int passadas = (int) (1 + Math.ceil((Math.log((maxId / (double) m))) / Math.log((double) n)));
		// double fds = (1 + (Math.log((maxId / m)) / Math.log(n)));
		// System.out.println(fds);

		return passadas;

	}

	private static void intercalacaoBalanceada(String[] inputFiles, int m) throws IOException {
		CRUD crud = new CRUD("../data/arquivo.bin");
		int maxId = crud.getMaxId();
		int n = inputFiles.length;
		String filename = "../data/arquivo";
		String[] arqS = new String[n * 2];
		for (int i = 0; i < n; i++) {
			arqS[i] = inputFiles[i];
		}
		int ap = n;
		for (int i = n + 1; i <= n * 2; i++) { // cria os n arquivos
			RandomAccessFile output = new RandomAccessFile(filename + (i) + "tmp.bin", "rw");
			arqS[ap] = filename + i + "tmp.bin";
			ap++;
			output.close();
		}
		long pos1 = 0;
		long pos2 = 0;
		long posOut1 = 0;
		long posOut2 = 0;
		byte[] ba1;
		byte[] ba2;
		int[] tamanhos = new int[n];
		Movie[] j_temp = new Movie[n];
		for (int i = 0; i < n; i++) {
			j_temp[i] = new Movie();
		}
		int passadas = calcularPassadas(m, n);
		int tamS = m * n;
		int proxS = m;
		int controle1 = 0;
		int controle2 = 0;
		//System.out.println("passadas = " + passadas);
		int a = (int) Math.ceil(((double) maxId + 1) / (double) n);
		//System.out.println("a = " + a);
		for (int i = 0; i < passadas; i++) {
			if (i % 2 == 0) {
				controle1 = 0;
				controle2 = 2;
			} else {
				controle1 = 2;
				controle2 = 0;
			}
			for (int k = 0; k < (a / proxS); k++) {
				//System.out.println("!!!!!!!!!!!!!");
				for (int j = 0; j < tamS; j++) {
					//System.out.println("c2 antes = " + controle2);
					if (controle2 > 3) {
						controle2 = 2;
					}
					//System.out.println("c2 depois = " + controle2);
					System.out.println("!!!! = " + arqS[controle2]);
					RandomAccessFile raf1 = new RandomAccessFile(arqS[controle1], "r");
					RandomAccessFile raf2 = new RandomAccessFile(arqS[controle1 + 1], "r");
					RandomAccessFile rafOut = new RandomAccessFile(arqS[controle2], "rw");
					if (j % 2 == 0) {
						rafOut.seek(posOut1);
					} else {
						rafOut.seek(posOut2);
					}
					System.out.println("pos 1 = " + pos1);
					System.out.println("pos 2 = " + pos2);
					raf1.seek(pos1);
					raf2.seek(pos2);

					tamanhos[0] = raf1.readInt();
					System.out.println("t0 = " + tamanhos[0]);
					ba1 = new byte[tamanhos[0]];
					raf1.read(ba1);
					j_temp[0].fromByteArray(ba1);

					if (raf2.getFilePointer() < raf2.length()){
						tamanhos[1] = raf2.readInt();
						System.out.println("t1 = " + tamanhos[1]);
						ba2 = new byte[tamanhos[1]];
						raf2.read(ba2);
						j_temp[1].fromByteArray(ba2);

						if (j_temp[0].id < j_temp[1].id) {
							rafOut.writeInt(ba1.length);
							rafOut.write(ba1);
							pos1 = pos1 + tamanhos[0] + 4;
						} else {
							rafOut.writeInt(ba2.length);
							rafOut.write(ba2);
							pos2 = pos2 + tamanhos[1] + 4;
						}
						
						if (j % 2 == 0) {
							posOut1 = rafOut.getFilePointer();
						} else {
							posOut2 = rafOut.getFilePointer();
						}
					}
				
				}
				controle2++;
			}
			tamS = tamS * n;
			proxS = proxS * n;
		}
	}

	public static void sort(String inputFiles1, String inputFiles2) throws IOException {
		byte ba1[];
		byte ba2[];
		int num = inputFiles2.charAt(inputFiles2.length());
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
		if (j_temp1.id < j_temp2.id) {
			rafOut.writeInt(ba1.length);
			rafOut.write(ba1);
		} else {
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
		output.seek(0);
		for (int i = 0; i < filmes.length; i++) {
			if (filmes[i].id != -1) {
				ba = new byte[tamanhos[i]];
				ba = filmes[i].toByteArray();
				output.writeInt(ba.length);
				output.write(ba);
			}
		}
		output.close();
	}
}
