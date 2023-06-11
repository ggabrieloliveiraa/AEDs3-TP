import java.io.RandomAccessFile;
import java.util.*;
import java.io.*;

public class CasamentoPadroes {
	int[] bordas; // tabela kmp
	int[] badChar; // caractere ruim
	int[] goodSuffix; // sufixo bom
	int operacoesKMP; // numero de operacoes(comparacoes) do kmp
	int operacoesBM;// numero de operacoes(comparacoes) do boyer moore

	CasamentoPadroes(String padrao) {
		this.bordas = calcularBordas(padrao);
		this.badChar = construirCharRuim(padrao.toCharArray());
		this.goodSuffix = construirSufixoBom(padrao.toCharArray());
		this.operacoesKMP = 0;
		this.operacoesBM = 0;
	}

	public void buscar(String padrao) throws Exception {
		// Obter o tempo inicial
		RandomAccessFile arquivo = new RandomAccessFile("../data/arquivo.bin", "rw");
		int maxId = arquivo.readInt();
		Movie j_temp = new Movie();
		List<Integer> indices = new ArrayList<Integer>();
		long tempoInicial = System.currentTimeMillis();
		for (int i = 0; i < maxId; i++) {
			int tamanho = arquivo.readInt();
			byte[] ba = new byte[tamanho];
			arquivo.read(ba);
			j_temp.fromByteArray(ba);
			String[] atributos = j_temp.getAtributos();
			for (int j = 0; j < 8; j++) {
				indices.addAll(buscarPadrao(atributos[j], padrao));
			}
		}
		// Obter o tempo final
		long tempoFinal = System.currentTimeMillis();
		// Calcular o tempo total de execução em milissegundos do KMP
		long tempoTotalKMP = tempoFinal - tempoInicial;
		int nKmp = indices.size();
		arquivo.seek(4);
		indices.removeAll(indices);
		tempoInicial = System.currentTimeMillis();
		for (int i = 0; i < maxId; i++) {
			int tamanho = arquivo.readInt();
			byte[] ba = new byte[tamanho];
			arquivo.read(ba);
			j_temp.fromByteArray(ba);
			String[] atributos = j_temp.getAtributos();
			for (int j = 0; j < 8; j++) {
				indices.addAll(search(atributos[j], padrao));
			}
		}
		// Obter o tempo final
		tempoFinal = System.currentTimeMillis();
		// Calcular o tempo total de execução em milissegundos do Boyer Moore
		long tempoTotalBM = tempoFinal - tempoInicial;
		int nBm = indices.size();
		if(nBm == nKmp) {
		System.out.println("Padrão encontrado " + nBm + " vezes (ambos os algoritmos encontraram o mesmo numero)");
		}
		else {
			System.out.println("Algo não ocorreu como o esperado! Os algoritmos nao tiveram o mesmo numero de padroes encontrados");
			System.out.println("Padrão encontrado " + nKmp + " vezes pelo algoritmo KMP");
			System.out.println("Padrão encontrado " + nBm + " vezes pelo algoritmo Boyer Moore");
		}
		System.out.println("-------------------------------------------------------");
		System.out.println("Tempo de execução do algoritmo KMP: " + tempoTotalKMP + " milissegundos");
		System.out.println("Numero de operaçoes realizadas pelo algoritmo KMP: " + this.operacoesKMP);
		System.out.println("-------------------------------------------------------");
		System.out.println("Tempo de execução do algoritmo Boyer Moore: " + tempoTotalBM + " milissegundos");
		System.out.println("Numero de operaçoes realizadas pelo algoritmo Boyer Moore: " + this.operacoesBM);
		System.out.println("-------------------------------------------------------");
		if (tempoTotalKMP == tempoTotalBM) {
			System.out.println("Os 2 algoritmos tiveram a mesma velocidade!");
		} else {
			if (tempoTotalKMP > tempoTotalBM)
				System.out.print("O algoritmo Boyer Moore foi ");
			else
				System.out.print("O algoritmo KMP foi ");
			System.out.println(
					Math.max(tempoTotalKMP, tempoTotalBM) - Math.min(tempoTotalKMP, tempoTotalBM) + " ms mais rapido");
		}
		if (operacoesKMP == operacoesBM) {
			System.out.println("Os 2 algoritmos tiveram o mesmo numero de operacoes!");
		} else {
			if (operacoesKMP > operacoesBM)
				System.out.print("O algoritmo Boyer Moore realizou ");
			else
				System.out.print("O algoritmo KMP realizou ");
			System.out.println(
					Math.max(operacoesKMP, operacoesBM) - Math.min(operacoesKMP, operacoesBM) + " operacoes a menos");
		}

	}
	// KMP

	/**
	 * Método para realizar a busca do padrão no texto utilizando o algoritmo KMP.
	 */
	public List<Integer> buscarPadrao(String texto, String padrao) {
		int n = texto.length();
		int m = padrao.length();

		List<Integer> indices = new ArrayList<>();

		int i = 0; // Índice para percorrer o texto
		int j = 0; // Índice para percorrer o padrão

		while (i < n) {
			this.operacoesKMP++;
			if (padrao.charAt(j) == texto.charAt(i)) {
				i++;
				j++;
				if (j == m) {
					// Padrão encontrado
					indices.add(i - j);
					j = bordas[j - 1];
				}
			} else if (j > 0) {
				j = bordas[j - 1];
			} else {
				i++;
			}
		}

		return indices;
	}

	/**
	 * Método para calcular as bordas do padrão utilizando o algoritmo KMP.
	 */
	public static int[] calcularBordas(String padrao) {
		int m = padrao.length();
		int[] bordas = new int[m];

		int i = 1; // Índice para percorrer o padrão
		int j = 0; // Índice para calcular as bordas

		while (i < m) {
			if (padrao.charAt(i) == padrao.charAt(j)) {
				j++;
				bordas[i] = j;
				i++;
			} else if (j > 0) {
				j = bordas[j - 1];
			} else {
				bordas[i] = 0;
				i++;
			}
		}

		return bordas;
	}

	// Boyer-Moore

	/**
	 * Método para construir a tabela de bad character do algoritmo Boyer-Moore.
	 */
	private static int[] construirCharRuim(char[] pattern) {
		int[] badChar = new int[256];
		Arrays.fill(badChar, pattern.length);

		for (int i = 0; i < pattern.length - 1; i++) {
			badChar[(int) pattern[i]] = pattern.length - 1 - i;
		}

		return badChar;
	}

	/**
	 * Método para construir a tabela de good suffix do algoritmo Boyer-Moore.
	 */
	private static int[] construirSufixoBom(char[] pattern) {
		int m = pattern.length;
		int[] goodSuffix = new int[m];
		int[] suff = suffixes(pattern);

		Arrays.fill(goodSuffix, m);

		for (int i = m - 1; i >= 0; i--) {
			if (suff[i] == i + 1) {
				for (int j = 0; j < m - 1 - i; j++) {
					if (goodSuffix[j] == m) {
						goodSuffix[j] = m - 1 - i;
					}
				}
			}
		}

		for (int i = 0; i < m - 1; i++) {
			goodSuffix[m - 1 - suff[i]] = m - 1 - i;
		}

		return goodSuffix;
	}

	/**
	 * Método para calcular os sufixos do padrão no algoritmo Boyer-Moore.
	 */
	private static int[] suffixes(char[] pattern) {
		int m = pattern.length;
		int[] suff = new int[m];
		int f = 0, g;

		suff[m - 1] = m;
		g = m - 1;

		for (int i = m - 2; i >= 0; i--) {
			if (i > g && suff[i + m - 1 - f] < i - g) {
				suff[i] = suff[i + m - 1 - f];
			} else {
				if (i < g) {
					g = i;
				}
				f = i;
				while (g >= 0 && pattern[g] == pattern[g + m - 1 - f]) {
					g--;
				}
				suff[i] = f - g;
			}
		}

		return suff;
	}

	/**
	 * Método de busca utilizando o algoritmo Boyer-Moore.
	 */
	public List<Integer> search(String text, String pattern) {
		List<Integer> matches = new ArrayList<>();

		int m = pattern.length();
		int n = text.length();

		char[] patternArr = pattern.toCharArray();
		char[] textArr = text.toCharArray();

		int shift = 0;
		while (shift <= (n - m)) {
			int j = m - 1;
			this.operacoesBM++;
			while (j >= 0 && patternArr[j] == textArr[shift + j]) {
				this.operacoesBM++;
				j--;
			}

			if (j < 0) {
				matches.add(shift);
				shift += (shift + m < n) ? goodSuffix[0] : 1;
			} else {
				shift += Math.max(badChar[(int) textArr[shift + j]] - m + 1 + j, goodSuffix[j]);
			}
		}

		return matches;
	}
}