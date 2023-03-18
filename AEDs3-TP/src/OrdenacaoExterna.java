import java.io.*;

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
		int[] pos = new int[n];
		for (int i = 0; i < n; i++) {
			pos[i] = 0; // preenche com 0
		}
		RandomAccessFile input = new RandomAccessFile(filename + ".bin", "rw");
		for (int i = 0; i < n; i++) { // cria os n arquivos
			RandomAccessFile output = new RandomAccessFile(filename + (i + 1) + "tmp.bin", "rw");
			arqs[i] = filename + (i + 1) + "tmp.bin";
			output.close();
		}
		int controle = 0;
		input.seek(4); // pula o cabeçalho que contem o maxId
		while (input.getFilePointer() < input.length()) { // rodar o arquivo binario inteiro
			int tamanhos[] = new int[m];
			Movie[] filmes = new Movie[m];
			for (int i = 0; i < m; i++) {
				filmes[i] = new Movie();
			}
			for (int i = 0; i < m; i++) {
				if (input.getFilePointer() < input.length() - 1) {
					tamanhos[i] = input.readInt();
					ba = new byte[tamanhos[i]];
					input.read(ba);
					filmes[i].fromByteArray(ba);
				}
			}
			quickSort(filmes, 0, filmes.length - 1); // ordena os blocos de tamanho m atributos em memoria principal
			for (int i = 0; i < filmes.length; i++) {
			}
			pos[controle % n] = distribuir(tamanhos, filmes, filename, (controle % n) + 1, pos[controle % n]);
			controle++;
		}
		intercalacaoBalanceada(arqs, m);
		input.close();
	}

	private static int calcularPassadas(int m, int n) throws IOException {
		CRUD crud = new CRUD("/home/gabriel/git/AEDs3-TP/AEDs3-TP/arquivo.bin");
		double maxId = crud.getMaxId();
		crud.fechar();
		int passadas = (int) (1 + Math.ceil((Math.log((maxId / (double) m))) / Math.log((double) n)));
		return passadas;

	}

	private static void intercalacaoBalanceada(String[] inputFiles, int m) throws IOException {
		CRUD crud = new CRUD("/home/gabriel/git/AEDs3-TP/AEDs3-TP/arquivo.bin");
		int maxId = crud.getMaxId();
		int n = inputFiles.length;
		String filename = "/home/gabriel/git/AEDs3-TP/AEDs3-TP/arquivo";
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
		byte[] ba1;
		byte[] ba2;
		byte[] baMenor;
		int fds = 0;
		Movie movieMenor = new Movie();
		int[] tamanhos = new int[n];
		Movie[] j_temp = new Movie[n];
		Movie tmp = new Movie();
		Movie tmp2 = new Movie();
		for (int i = 0; i < n; i++) {
			j_temp[i] = new Movie();
		}
		String arquivoFinal = "";
		int passadas = calcularPassadas(m, n);
		int lastDance = 0;
		int tamS = m * n;
		int proxS = m;
		int controle1 = 0;
		int controle2 = 0;
		int flag = 0;
		int[] cb = new int[n];
		int arqMenor = 0;
		int[] posOut = new int[n];
		for (int i = 0; i < n; i++) {
			posOut[i] = 0; // preenche ponteiros com 0
		}
		double a = Math.ceil(((double) maxId + 1) / (double) n);
		RandomAccessFile[] rafIn = new RandomAccessFile[n * 2];
		for (int i = 0; i < n * 2; i++) {
			rafIn[i] = new RandomAccessFile(arqS[i], "r");
		}
		for (int i = 0; i < passadas; i++) {
			if (i % 2 == 0) {
				controle1 = 0;
				controle2 = n;
			} else {
				controle1 = n;
				controle2 = 0;
			}
			for (int f = 0; f < n; f++) {
				rafIn[f] = new RandomAccessFile(arqS[controle1 + f], "r");
			}
			double dentroInter = Math.ceil(((double) a / (double) proxS));
			if (dentroInter == 1) {
				flag++;
			}
			for (int k = 0; k < dentroInter && flag <= 1; k++) { // quantas vezes vai trocar o arquivo de saida
				for (int j = 0; j < tamS; j++) { // passar pelos registros(tamS = bloco * n)
					if (controle2 > n + 1 && controle1 == 0) {
						controle2 = n;
					}
					RandomAccessFile rafOut = new RandomAccessFile(arqS[controle2], "rw");
					rafOut.seek(posOut[k % n]); // seta o ponteiro do arquivo de saida certo
					for (int b = 0; b < n; b = b + 2) { // roda os n caminhos procurando o menor id
						if (b == 0) { // se é o primeiro, seta o menor pra ser o segundo
							movieMenor = j_temp[b + 1];
						}
						if (b + 1 == n) { // se n for ímpar, quando chegar na última comparação não terá um par de
											// blocos
							tamanhos[b] = rafIn[b].readInt();
							ba1 = new byte[tamanhos[b]];
							rafIn[b].read(ba1);
							j_temp[b].fromByteArray(ba1);
							if ((j_temp[b].id < movieMenor.id && cb[b] != tamS / n) || cb[b + 1] == tamS / n) {
								movieMenor = j_temp[b];
								arqMenor = b;
							}
						} else if (rafIn[b].getFilePointer() >= rafIn[b].length() && rafIn[b].length() != 0) {
							// se arquivo b já tiver acabado
							tamanhos[b + 1] = rafIn[b + 1].readInt();
							ba2 = new byte[tamanhos[b + 1]];
							rafIn[b + 1].read(ba2);
							j_temp[b + 1].fromByteArray(ba2);
							if ((j_temp[b + 1].id < movieMenor.id && cb[b + 1] != tamS / n) || cb[b] == tamS / n) {
								movieMenor = j_temp[b + 1];
								arqMenor = b + 1;
							}
						} else if (rafIn[b + 1].getFilePointer() >= rafIn[b + 1].length()
								// se arquivo b+1 já tiver acabado
								&& rafIn[b + 1].length() != 0) {
							tamanhos[b] = rafIn[b].readInt();
							ba1 = new byte[tamanhos[b]];
							rafIn[b].read(ba1);
							j_temp[b].fromByteArray(ba1);
							if ((j_temp[b].id < movieMenor.id && cb[b] != tamS / n) || cb[b + 1] == tamS / n) {
								movieMenor = j_temp[b];
								arqMenor = b;
							}
						} else { // se os dois arquivos ainda tiverem registros
							// System.out.println("fp1 = " + rafIn[b].getFilePointer());
							// System.out.println("length1 = " + rafIn[b].length());
							tamanhos[b] = rafIn[b].readInt();
							// System.out.println("tamanho1 = " + tamanhos[b]);
							// System.out.println("tamanho0 = " + tamanhos[b + 1]);
							ba1 = new byte[tamanhos[b]];
							rafIn[b].read(ba1);
							j_temp[b].fromByteArray(ba1);
							// System.out.println("fp2 = " + rafIn[b + 1].getFilePointer());
							// System.out.println("length2 = " + rafIn[b + 1].length());
							tamanhos[b + 1] = rafIn[b + 1].readInt();
							// System.out.println("tamanho2 = " + tamanhos[b + 1]);
							ba2 = new byte[tamanhos[b + 1]];
							rafIn[b + 1].read(ba2);
							// System.out.println("k = " + k);
							j_temp[b + 1].fromByteArray(ba2);
							baMenor = ba2;
							if ((j_temp[b].id < movieMenor.id && cb[b] != tamS / n) || cb[b + 1] == tamS / n) {
								// se o id desse arquivo for o menor e se ainda não tiver passado por todos os
								// blocos
								// System.out.println("j1 = " + j);
								j_temp[b].fromByteArray(ba1);
								movieMenor = j_temp[b];
								baMenor = ba1;
								arqMenor = b;
							} else if (cb[b + 1] != tamS / n || cb[b] == tamS / n) {
								// System.out.println("j2 = " + j);
								// se ainda nao tiver passado por todos os blocos
								j_temp[b + 1].fromByteArray(ba2);
								movieMenor = j_temp[b + 1];
								arqMenor = b + 1;
							}
						}
					}
					if (j % 2 == 0) {
						tmp2 = movieMenor;
					}
					baMenor = movieMenor.toByteArray();
					if (tmp.id != movieMenor.id) // confere se o filme a ser inserido nao e
																				// o msm da ultima insercao
					{
						rafOut.writeInt(baMenor.length);
						rafOut.write(baMenor); // escreve o menor encontrado no arquivo de saida
						//System.out.println("j = " +  j);
						//System.out.println(movieMenor.id);
					}
					cb[arqMenor]++; // mostra que ja adicionou deste arquivo de entrada
					tmp.fromByteArray(baMenor);
					for (int b = 0; b < n; b++) {
						if (b != arqMenor) { // volta com o ponteiro de todos, menos o que inseriu no arquivo
							rafIn[b].seek(rafIn[b].getFilePointer() - tamanhos[b] - 4);
						}
					}
					posOut[k % n] = (int) rafOut.getFilePointer();
					arquivoFinal = arqS[controle2]; // ultimo arquivo salvo
				}
				for (int c = 0; c < cb.length; c++) {
					cb[c] = 0; // reinicia o controlador de quantidade de entidades passadas por arquivo
				}
				// System.out.println(k);
				controle2++;
			}
			proxS = proxS * n;
			System.out.println("tamS = " + tamS);
			tamS = tamS * n;
			if (tamS > maxId) {
				fds++;
			}
			for (int po = 0; po < n; po++) {
				posOut[po] = 0; // reseta ponteiro do arquivo de saida
			}
		}
		RandomAccessFile in = new RandomAccessFile(arquivoFinal, "r");
		RandomAccessFile out = new RandomAccessFile("/home/gabriel/git/AEDs3-TP/AEDs3-TP/arquivo.bin", "rw");
		out.writeInt(maxId);
		byte[] buffer = new byte[1024];

		int bytesLidos;
		while ((bytesLidos = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesLidos);
		}
		for (int i = 0; i < arqS.length; i++) // deleta os arquivos
		{
			File arquivo = new File(arqS[i]);
			arquivo.delete();
		}
		in.close();
		out.close();
	}

	private static int distribuir(int[] tamanhos, Movie[] filmes, String filename, int n, int pos) throws IOException {
		RandomAccessFile output = new RandomAccessFile(filename + n + "tmp.bin", "rw");
		byte ba[];
		output.seek(pos);
		for (int i = 0; i < filmes.length; i++) {
			if (filmes[i].id != -1) {
				ba = new byte[tamanhos[i]];
				ba = filmes[i].toByteArray();
				output.writeInt(ba.length);
				output.write(ba);
			}
		}
		int resp = (int) output.getFilePointer();
		output.close();
		return resp;
	}
}