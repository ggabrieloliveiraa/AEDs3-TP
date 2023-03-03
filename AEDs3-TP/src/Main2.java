import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main2 {

	public static void main(String[] args) {
		List<Movie> filmes = readCsv("../data/movies.csv");
		byte ba[];
		int len;
		Movie j_temp = new Movie();
		try {

			RandomAccessFile fos = new RandomAccessFile("../data/arquivo.bin", "rw");

			for (int i = 0; i < filmes.size(); i++) {
				//System.out.println("Posicao do registro: " + fos.getFilePointer());
				ba = filmes.get(i).toByteArray();
				if(i < 10) {
				//System.out.println(ba.length);
				}
				fos.writeInt(ba.length); // tamanho do registro em bytes
				fos.write(ba); // vetor de bytes que descrevem o objeto
			}
			fos.seek(860381);
			len = fos.readInt();
			System.out.println("len = " + len);
			ba = new byte[len];
			fos.read(ba);
			j_temp.fromByteArray(ba);
			System.out.println(j_temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		interfac();
		// readBinary();
	}

	public static void interfac () {
		Scanner scanner = new Scanner(System.in);
		try{
		CRUD crud = new CRUD("../data/arquivo.bin");

		while (true) {
            System.out.println("Selecione uma operação:");
            System.out.println("1 - Listar");
            System.out.println("2 - Inserir");
            System.out.println("3 - Atualizar");
            System.out.println("4 - Excluir");
            System.out.println("0 - Sair");

			int id = 0;
			Movie m_temp = new Movie();
            int opcao = scanner.nextInt();
            scanner.nextLine(); // limpa o buffer do scanner
            switch (opcao) {
                case 1:
				    System.out.println("Qual ID você deseja mostrar?");
					id = scanner.nextInt();
					m_temp = crud.buscar(id);
                    if (m_temp!=null){
						System.out.println(m_temp);
					}
					else{
						System.out.println("ERRO: ID não encontrado!");
					}

                    break;
                case 2:
					m_temp = getMovie();

				    crud.inserir(m_temp.toByteArray());
                    //inserirPessoa(scanner, listaPessoas);
                    break;
                case 3:
				    //crud.atualizar(getMovie());
					break;
				case 4:
					System.out.println("Qual ID você deseja remover?");
					id = scanner.nextInt();
					m_temp = crud.remover(id);
										
					break;
				case 0:
					System.out.println("Saindo...");
					crud.fechar();
					return;
				default:
					System.out.println("Opção inválida!");
		}
	}
}catch (IOException e){
		e.printStackTrace();
	}
}


    public static Movie getMovie () {
		Scanner sc = new Scanner(System.in);

		
		String title, director;
		
		float rating;
		//java.util.Date year;
		int id = 0;

		System.out.println("Digite o título do filme: ");
		title = sc.nextLine();

		System.out.println("Digite o diretor do filme: ");
		director = sc.nextLine();

		System.out.println("Escolha o certificado de classificação etária para o filme:");
        System.out.println("1 - A (all ages)");
        System.out.println("2 - PG-13");
        System.out.println("3 - R");
        System.out.println("4 - U");
        System.out.println("5 - UA");

        int escolha = sc.nextInt();

        String certificado = " ";
        switch (escolha) {
            case 1:
                certificado = "A";
                break;
            case 2:
                certificado = "PG-13";
                break;
            case 3:
			certificado = "R";
                break;
            case 4:
                certificado = "U";
                break;
            case 5:
                certificado = "UA";
                break;
            default:
                System.out.println("Opção inválida!");
                break;
		}

		System.out.println("Digite quantos gêneros o filmes vai ter");
		int k = sc.nextInt();

		String[] genre = new String[k];

		for (int i = 0; i < k; i++){
			System.out.println("Digite o " + i + "º gênero + ENTER");
			genre[i] = sc.nextLine();
		}

		System.out.println("Digite a avaliação do filme, separado por vírgula");
		rating = sc.nextFloat(); 

		System.out.println("Digite o ano de lançamento do filme");
		int date = sc.nextInt();
		Date year = new Date();// por enquanto deixei assim(atributos[1])
		id = 666666;

		Movie filme = new Movie(false, id, title, year, certificado, genre, rating, director);
	
		return filme;
		
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
				Date year = new Date();// por enquanto deixei assim(atributos[1])
				String certificate = atributos[2];
				String[] genre = atributos[3].replaceAll("^\"|\"$", "").split(",");
				float rating = Float.parseFloat(atributos[4]);
				String director = atributos[5];
				Movie filme = new Movie(false, id, title, year, certificate, genre, rating, director);
				id++;
				filmes.add(filme);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filmes;
	}

	public static void readBinary() {
		File file = new File("../data/arquivo.bin");

		try {
			FileInputStream inputStream = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()]; // cria um array de bytes com o tamanho do arquivo
			int bytesRead = inputStream.read(bytes); // lê o arquivo para o array de bytes

			// Imprime o array de bytes em formato hexadecimal
			for (int i = 0; i < bytesRead; i++) {
				System.out.printf("%02X ", bytes[i]);
			}

			inputStream.close(); // fecha o arquivo
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}