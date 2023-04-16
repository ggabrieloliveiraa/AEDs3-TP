import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.*;
import java.io.File;

public class CRUD {
	private RandomAccessFile file;
	Hash hash;
	ListaInvertida listaInvertida;
	boolean isHash;

	public CRUD(String nomeArquivo, int tipo) throws Exception {
		String tmp = nomeArquivo;
		this.file = new RandomAccessFile(tmp, "rw");
		if (tipo == 1) {
			isHash = true;
			if (file.length() == 0) {
				cargaInicial();
				this.hash = new Hash(false);
				System.out.println("CARGA INICIAL REALIZADA PARA CRIAR ARQUIVOS HASH");
			} else {
				this.hash = new Hash(false);
			}
		} else {
			isHash = false;
		}
		if (tipo == 1) {
			this.listaInvertida = new ListaInvertida(false);
		}
	}

	public void fechar() throws Exception {
		file.close();
	}

	public int getMaxId() throws Exception {
		file.seek(0);
		return (file.readInt());
	}

	String getGeneros() throws Exception {
		file.readBoolean(); // lapide
		int id = file.readInt(); // id
		// System.out.println(id);
		file.readUTF(); // title
		file.readUTF(); // director
		// file.seek(file.getFilePointer() + 9); // certificado(string tamanho fixo)
		byte[] stringBytes = new byte[9];
		file.readFully(stringBytes);
		file.readInt();
		String allGen = file.readUTF();
		return allGen;
	}

	public ArrayList<String> lerGeneros() throws Exception { // cria uma lista sem repetir com todos os generos que tem
		int posicao = 4;
		int tamanho = 0;
		ArrayList<String> listaGen = new ArrayList<>();

		while (posicao < file.length()) { // sempre da problema na ultima iteração por algum motivo

			file.seek(posicao);
			tamanho = file.readInt();
			String allGen = getGeneros();
			// System.out.println("ag = " + allGen);
			String[] gen = allGen.split(",");
			for (int i = 1; i < gen.length; i++) {
				gen[i] = gen[i].substring(1); // tirar o espaço antes do gen dps do primeiro(passar isso pro movie dps)
				// System.out.println("gi = " + gen[i]);
			}
			for (int i = 0; i < gen.length; i++) {
				if (!listaGen.contains(gen[i])) {
					System.out.println("CRUD // " + gen[i]);
					listaGen.add(gen[i]);
				}
			}
			posicao += tamanho + 4;
		}
		for (int i = 0; i < listaGen.size(); i++) {
			// System.out.println(listaGen.get(i));
		}
		return listaGen;

	}

	public ArrayList<PalavraIndexada> getPalavrasIndexadas() throws Exception {
		int posicao = 4;
		int tamanho = 0;
		ArrayList<String> dicionario = lerGeneros();
		ArrayList<PalavraIndexada> pi = new ArrayList<PalavraIndexada>();
		for (int i = 0; i < dicionario.size(); i++) {
			pi.add(new PalavraIndexada(dicionario.get(i))); // adiciona as palavras do dicionario
		}
		while (posicao < file.length()) {
			file.seek(posicao);
			// System.out.println("p = " + posicao);
			tamanho = file.readInt();
			// System.out.println("t = " + tamanho);
			// System.out.println("fl = " + file.length());
			file.readBoolean(); // lapide
			int id = file.readInt(); // id
			file.readUTF(); // title
			file.readUTF(); // director
			file.seek(file.getFilePointer() + 9); // certificado(string tamanho fixo)
			int quantGen = file.readInt();
			String allGen = file.readUTF();
			String[] gen = allGen.split(",");
			for (int i = 1; i < quantGen; i++) {
				gen[i] = gen[i].substring(1); // tirar o espaço antes do gen dps do primeiro(passar isso pro movie dps)
			}
			for (int i = 0; i < quantGen; i++) {
				for (int j = 0; j < pi.size(); j++) {
					if (pi.get(j).palavra.equals(gen[i])) { // quando a palavra for igual a do dicionario, adicionar id
						pi.get(j).addId(id);
					}
				}
			}
			posicao += tamanho + 4;
		}
		return pi;
	}

	public void buscarPorListaInvertida(String query) throws Exception {
		ArrayList<Integer> ids = listaInvertida.buscar(query);
		buscarPorIds(ids);
	}

	public void buscarPorIds(ArrayList<Integer> ids) throws Exception {
		int posicao = 4;
		int tamanho = 0;
		byte ba[];
		for (int i = 0; i < ids.size(); i++) {
			// System.out.println("id = " + ids.get(i));
		}
		List<Movie> movies = new ArrayList<Movie>();
		for (int i = 0; i < ids.size(); i++) {
			posicao = hash.buscar(ids.get(i), false);
			if (posicao != -1) {
				// System.out.println("pos = " + posicao);
				file.seek(posicao);
				tamanho = file.readInt();
				ba = new byte[tamanho];
				// System.out.println("posicao = " + file.getFilePointer());
				file.read(ba);
				Movie j_temp = new Movie();
				j_temp.fromByteArray(ba);
				movies.add(j_temp);
			}
		}
		for (int i = 0; i < movies.size(); i++) {
			System.out.println(movies.get(i));
		}
	}

	public void inserir(byte[] ba, boolean update) throws Exception {
		// variaveis que vao ser usadas para posicionar e id
		int posicao = 4;
		int tmp = 0;
		int tamanho = 0;

		// criar o objeto que vai ser inserido
		Movie j_temp = new Movie();
		j_temp.fromByteArray(ba);

		// percorrer o arquivo todo cacando um espaco
		while (posicao < file.length() || posicao < 0) {

			file.seek(posicao);
			tamanho = file.readInt();

			boolean lapide = file.readBoolean();

			if (lapide == true && tamanho >= ba.length) {

				if (!update) {
					int idMaximo = getMaxId() + 1;
					file.seek(0);
					file.writeInt(idMaximo);
					j_temp.id = getMaxId();
					for (int i = 0; i < j_temp.genre.length; i++) {
						System.out.println("????????");
						listaInvertida.inserir(j_temp.genre[i], j_temp.id); // inserir generos na lista invertida
					}

				}
				if (isHash) {
					hash.inserir(j_temp.id, false, posicao); // inserir no arquivo de indice hash

				}

				file.seek(posicao + 4); // pular o tamanho

				byte[] arr = j_temp.toByteArray();
				file.write(arr);

				System.out.println("id do filme inserido: " + j_temp.id);
				return;
			}

			posicao += tamanho + 4;

			tmp++;
		}

		if (!update) {
			int idMaximo = getMaxId() + 1;
			file.seek(0);
			file.writeInt(idMaximo);
			j_temp.id = getMaxId();

		}

		if (isHash) {
			hash.inserir(j_temp.id, false, posicao); // inserir no arquivo de indice hash
		}

		file.seek(posicao);
		file.writeInt(ba.length);// escrever o tamanho

		byte[] arr = j_temp.toByteArray();
		file.write(arr);
		if (!update) {
			for (int i = 0; i < j_temp.genre.length; i++) {
				System.out.println("????????");
				listaInvertida.inserir(j_temp.genre[i], j_temp.id); // inserir generos na lista invertidaa
			}
		}

		System.out.println("id do filme inserido: " + j_temp.id);
		return;

	}

	public Movie buscar(int id) throws Exception {
		int posicao;
		if (isHash) {
			posicao = hash.buscar(id, false); // buscar no arquivo de indice hash
		} else {
			posicao = apontar(id);
		}
		int tamanho;

		// System.out.println(posicao);

		byte ba[];
		Movie j_temp = new Movie();

		if (posicao != -1) {

			file.seek(posicao);
			tamanho = file.readInt();
			ba = new byte[tamanho];
			// System.out.println("posicao = " + file.getFilePointer());
			file.read(ba);
			j_temp.fromByteArray(ba);
			return j_temp;
		}
		return null;
	}

	public Movie buscar(String title) throws Exception {
		int posicao = apontar(title);
		int tamanho;

		byte ba[];
		Movie j_temp = new Movie();

		if (posicao != -1) {
			tamanho = file.readInt();
			ba = new byte[tamanho];
			file.seek(posicao + 4);
			file.read(ba);
			j_temp.fromByteArray(ba);
			return j_temp;
		}
		return null;
	}

	public Movie getFromPos(int pos) throws Exception {
		file.seek(pos);
		int tamanho = file.readInt();
		byte[] ba = new byte[tamanho];
		file.read(ba);
		Movie j_temp = new Movie();
		j_temp.fromByteArray(ba);
		return j_temp;
	}

	public void atualizar(int id) throws Exception {
		Scanner sc = new Scanner(System.in);
		Movie j_temp = new Movie();
		int posicao;
		if (isHash) {
			posicao = hash.buscar(id, false); // apontar no arquivo de indice hash
			// System.out.println(posicao);
		} else {
			posicao = apontar(id);
		}

		j_temp = getFromPos(posicao);
		String tmp = "";
		// Percorre o arquivo em busca do movie com o ID especificado

		System.out.println("qual atributo do filme voce deseja atualizar?");

		while (true) {
			System.out.println("Selecione um atributo:");
			System.out.println("1 - Título = " + j_temp.title);
			System.out.println("2 - Diretor = " + j_temp.director);
			System.out.println("3 - Certificado = " + j_temp.certificate);
			System.out.println("4 - Genero(s) = " + Arrays.toString(j_temp.genre));
			System.out.println("5 - Nota = " + j_temp.rating);
			System.out.println("6 - Ano Lançamento = " + j_temp.year);
			System.out.println("0 - Sair");

			int opcao = sc.nextInt();
			sc.nextLine(); // limpa o buffer do scanner
			switch (opcao) {

			case 1:
				System.out.println("Novo título: ");
				tmp = sc.nextLine();

				j_temp.title = tmp;

				break;

			case 2:
				System.out.println("Novo diretor: ");
				tmp = sc.nextLine();

				j_temp.director = tmp;

				break;

			case 3:
				System.out.println("Escolha o certificado de classificação etária para o filme:");
				System.out.println("1 - A (all ages)");
				System.out.println("2 - PG-13");
				System.out.println("3 - R");
				System.out.println("4 - U");
				System.out.println("5 - UA");

				int escolha = sc.nextInt();
				switch (escolha) {
				case 1:
					tmp = "A";
					break;
				case 2:
					tmp = "PG-13";
					break;
				case 3:
					tmp = "R";
					break;
				case 4:
					tmp = "U";
					break;
				case 5:
					tmp = "UA";
					break;
				default:
					System.out.println("Opção inválida!");
					// break;
				}

				j_temp.certificate = tmp;

				break;

			case 4:
				// ler os generos
				System.out.println("Digite quantos gêneros o filmes vai ter");
				int k = sc.nextInt();

				String[] genre = new String[k];
				sc.nextLine();
				for (int i = 0; i < k; i++) {
					System.out.println("Digite o " + (i + 1) + "º gênero + ENTER");
					genre[i] = sc.nextLine();
				}
				List<String> generosAntigos = Arrays.asList(j_temp.genre);
				List<String> novosGeneros = Arrays.asList(genre);
				List<String> paraInserir = new ArrayList<String>(novosGeneros);
				paraInserir.removeAll(generosAntigos);
				for (int i = 0; i < paraInserir.size(); i++) {
					listaInvertida.inserir(paraInserir.get(i), j_temp.id); // inserir generos na lista invertida
				}
				generosAntigos = Arrays.asList(j_temp.genre);
				novosGeneros = Arrays.asList(genre);
				List<String> paraRemover = new ArrayList<String>(generosAntigos);
				paraRemover.removeAll(novosGeneros);
				for (int i = 0; i < paraRemover.size(); i++) {
					listaInvertida.remover(paraRemover.get(i), j_temp.id);
				}
				// verificar se cabe
				file.seek(posicao + 9 + j_temp.title.length() + j_temp.director.length() + j_temp.certificate.length());

				j_temp.genre = genre;

				break;

			case 5:
				float rating;
				System.out.println("Digite a avaliação do filme, separado por vírgula");
				rating = sc.nextFloat();
				j_temp.rating = rating;
				break;

			case 6:
				System.out.println("Digite o ano de lançamento do filme");
				int date = sc.nextInt();
				j_temp.year = Movie.parseDate(date);
				break;

			case 0:
				if (isHash) {
					this.isHash = false;
					// System.out.println("t arquivo = " + file.length());
					remover(id, true);
					inserir(j_temp.toByteArray(), true);
					this.isHash = true;
					int newPos = (apontar(id));
					hash.atualizar(id, newPos);
				} else {
					remover(id, true);
					inserir(j_temp.toByteArray(), true);
				}
				System.out.println("Saindo...");
				return;
			default:
				System.out.println("Opção inválida!");
			}
		}

	}

	public Movie remover(int id, boolean isUpdate) throws Exception {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao;
		if (isHash) {
			posicao = hash.buscar(id, true); // remover no arquivo de indice hash
		} else {
			posicao = apontar(id);
		}

		Movie j_temp = new Movie();
		if (posicao != -1) {
			byte ba[];
			file.seek(posicao);
			int tamanho = file.readInt();

			ba = new byte[tamanho];
			file.read(ba);

			file.seek(posicao + 4);
			file.writeBoolean(true); // marca o movie como removido

			j_temp.fromByteArray(ba);
			if (!isUpdate) {
				for (int i = 0; i < j_temp.genre.length; i++) {
					listaInvertida.remover(j_temp.genre[i], id);
				}
			}

			System.out.println("ITEM REMOVIDO!");
		} else {
			System.out.println("ERRO: ITEM NÃO ENCONTRADO");
		}
		return j_temp;
	}

	/*
	 * int apontar - aponta pro inicio do registro
	 * 
	 * @param int id - id do objeto procurado
	 * 
	 * @return int - false = -1 true = posicao
	 */
	public int apontar(int id) throws Exception {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = 4;
		while (posicao < file.length()) {
			file.seek(posicao);
			int tamanho = file.readInt();
			boolean lapide = file.readBoolean();
			file.seek(posicao + 5);
			int registroId = file.readInt();
			if (lapide == false && registroId == id) {
				return posicao;
			}
			posicao += tamanho + 4;
		}
		return -1;
	}

	public int apontar(String title) throws Exception {
		// Percorre o arquivo em busca do movie com o ID especificado
		int posicao = 4;
		String registroTitle = "";
		while (posicao < file.length()) {
			file.seek(posicao);
			int tamanho = file.readInt();
			boolean lapide = file.readBoolean();
			file.seek(posicao + 5);
			file.readInt();
			registroTitle = file.readUTF();
			if (lapide == false && registroTitle.equals(title)) {
				return posicao;
			}
			posicao += tamanho + 4;
		}
		return -1;
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
				int year = 0;
				if (atributos[1].length() == 4) {
					year = Integer.parseInt(atributos[1]);
				} else {
					year = Integer.parseInt(atributos[1].substring(atributos[1].length() - 4));
				}
				String certificate = atributos[2];
				String[] genre = atributos[3].replaceAll("^\"|\"$", "").split(",");
				float rating = Float.parseFloat(atributos[4]);
				String director = atributos[5];
				Movie filme = new Movie(false, id, title, year, certificate, genre, rating, director);
				id++;
				filmes.add(filme);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filmes;
	}

	public void cargaInicial() {
		List<Movie> filmes = readCsv("../data/movies.csv");
		byte ba[];
		try {
			if (file.length() == 0) {
				file.seek(0);
				file.writeInt(filmes.size() - 1); // cabeçalho
			} else {
				file.seek(4);
			}
			try {
				Hash hash = new Hash(true);
				for (int i = 0; i < filmes.size(); i++) {
					ba = filmes.get(i).toByteArray();
					// System.out.println(filmes.get(i));
					hash.inserir(filmes.get(i).id, false, (int) file.getFilePointer());
					file.writeInt(ba.length); // escreve tamanho da entidade

					file.write(ba); // escreve o byte de arrays da entidade
				}
				this.listaInvertida = new ListaInvertida(true);
				// file.seek(pos1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public void mostrarTudoBin() throws Exception {
		int pos = 0;
		byte ba[];
		Movie j_temp = new Movie();
		while (pos < file.length()) {
			int tamanho = file.readInt();
			System.out.println("t = " + tamanho);
			ba = new byte[tamanho];
			file.read(ba);
			j_temp.fromByteArray(ba);
			System.out.println(j_temp);
			pos += tamanho;
		}
	}

	public void mostrarTudo(String filename, int pos) throws Exception {
		RandomAccessFile arq = new RandomAccessFile(filename, "rw");
		Movie j_temp = new Movie();
		arq.seek(pos);
		int tamanho = 0;
		int i = 0;

		for (i = 0; arq.getFilePointer() < arq.length(); i++) {
			if (arq.getFilePointer() < arq.length()) {
				tamanho = arq.readInt();
				byte ba[];
				ba = new byte[tamanho];
				arq.read(ba);

				j_temp.fromByteArray(ba);
				System.out.println(j_temp);
			} else {
				System.out.println("segundo registro = " + j_temp);
				System.out.println("lido " + i + " registros");
				return;
			}
		}

		System.out.println("lido " + i + " registros");
		arq.close();
	}

	public void cargaInicialRandom() {
		int[] id = aleatorizar(10064);
		List<Movie> filmes = readCsv("../data/movies.csv", id);
		byte ba[];
		try {
			if (file.length() == 0) {
				file.seek(0);
				file.writeInt(filmes.size() - 1); // cabeçalho
			} else {
				file.seek(4);
			}
			for (int i = 0; i < filmes.size(); i++) {
				ba = filmes.get(i).toByteArray();
				if (i < 10) {
				}
				file.writeInt(ba.length); // escreve tamanho da entidade
				file.write(ba); // escreve o byte de arrays da entidade
				// inserir(ba);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static List<Movie> readCsv(String filename, int[] id) {
		List<Movie> filmes = new ArrayList<>();
		int controle = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			br.readLine(); // Ignora a primeira linha que contém cabeçalhos de coluna
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String[] atributos = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				// o regex acima divide a linha em campos, ignorando as vírgulas entre aspas
				String title = atributos[0];
				int year = 0;
				if (atributos[1].length() == 4) {
					year = Integer.parseInt(atributos[1]);
				} else {
					year = Integer.parseInt(atributos[1].substring(atributos[1].length() - 4));
				}
				String certificate = atributos[2];
				String[] genre = atributos[3].replaceAll("^\"|\"$", "").split(",");
				float rating = Float.parseFloat(atributos[4]);
				String director = atributos[5];
				Movie filme = new Movie(false, id[controle], title, year, certificate, genre, rating, director);
				controle++;
				filmes.add(filme);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filmes;
	}

	public int[] aleatorizar(int max) {
		int[] numbers = new int[10065];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = i;
		}
		Random rand = new Random();
		for (int i = numbers.length - 1; i > 0; i--) {
			int j = rand.nextInt(i + 1);
			int temp = numbers[i];
			numbers[i] = numbers[j];
			numbers[j] = temp;
		}

		return numbers;
	}

}