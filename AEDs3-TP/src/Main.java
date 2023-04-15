import java.io.IOException;
import java.util.Scanner;


public class Main {

	public static void main(String[] args) {
		init();
	}

	public static void init (){
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("Selecione uma operação:");
			System.out.println("1 - Sequencial + Ordenação externa");
			System.out.println("2 - Hash");
			System.out.println("0 - Sair");

			int opcao = scanner.nextInt();
			scanner.nextLine(); // limpa o buffer do scanner
			switch (opcao) {

			// listar
			case 1: 
				interfac(0);
				break;
			case 2:
				interfac(1);
				break;
			case 0:
				System.out.println("Saindo...");
				return;
			default:
				System.out.println("Opção inválida!");
			}

		}

	}

	public static void interfac(int tipo) {
		Scanner scanner = new Scanner(System.in);
		
		try {
			CRUD crud = new CRUD("../data/arquivo.bin", tipo);

			while (true) {
				System.out.println("Selecione uma operação:");
				System.out.println("1 - Listar");
				System.out.println("2 - Inserir");
				System.out.println("3 - Atualizar");
				System.out.println("4 - Excluir");
				System.out.println("5 - Mostrar tudo");
				System.out.println("6 - Carga inicial");
				System.out.println("7 - Carga inicial com IDs aleatorios");
				System.out.println("8 - Ordenação externa");
				//System.out.println("9 - Gerar lista invertida(gêneros)");
				System.out.println("0 - Sair");
		
				int id = 0;
				Movie m_temp = new Movie();
				int opcao = scanner.nextInt();
				scanner.nextLine(); // limpa o buffer do scanner
				switch (opcao) {

				// listar
				case 1:
					System.out.println("1 - Buscar por ID");
					if (tipo == 0){
						System.out.println("2 - Buscar por título");
					}
					System.out.println("3 - Buscar por gênero");

					int opco = scanner.nextInt();
					scanner.nextLine(); // limpa o buffer do scanner
					switch (opco) {
					case 1:
						System.out.println("Maior ID = " + crud.getMaxId());
						System.out.println("Qual ID você deseja mostrar?");
						id = scanner.nextInt();
						m_temp = crud.buscar(id);
						break;
					case 2:
						System.out.println("Qual título você deseja mostrar?");
						String title = scanner.nextLine();
						m_temp = crud.buscar(title);
						break;
					case 3:
						System.out.println("Você quer pesquisar por quais gêneros?(usa lista invertida)");
						String query = scanner.nextLine();
						crud.buscarPorListaInvertida(query);
						break;
					default:
						System.out.println("ERRO: opcao invalida");
					}
					if (m_temp != null) {
						System.out.println(m_temp);
					} else {
						System.out.println("ERRO: filme não encontrado!");
					}

					break;

				// inserir
				case 2:
					m_temp = getMovie();

					crud.inserir(m_temp.toByteArray(), false);
					break;

				// atualizar
				case 3:
					System.out.println("Qual ID do filme que voce deseja atualizar?");
					id = scanner.nextInt();
					crud.atualizar(id);
					break;

				// remover
				case 4:
					System.out.println("Qual ID você deseja remover?");
					id = scanner.nextInt();
					m_temp = crud.remover(id);
					break;
				case 5:
					crud.mostrarTudo("../data/arquivo.bin", 4);
					break;
				case 6:
					crud.cargaInicial();
					System.out.println("Carga inicial realizada!");
					System.out.println("Maior ID = " + crud.getMaxId());
					break;
				case 7:
					crud.cargaInicialRandom();
					System.out.println("Carga inicial realizada!");
					System.out.println("Maior ID = " + crud.getMaxId());
					break;
				case 8:
					try {
						OrdenacaoExterna.externalSort("arquivo", 1260, 2); //estavel quando m > 1260 e n = 2
						System.out.println("ARQUIVO ORDENADO!");
					} catch (IOException e) {
						e.printStackTrace();
					}
					interfac(tipo);
					return;
				case 0:
					System.out.println("Saindo...");
					crud.fechar();
					return;
				default:
					System.out.println("Opção inválida!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// pegar cada atributo do filme por input do terminal
	public static Movie getMovie() {
		Scanner sc = new Scanner(System.in);

		String title, director;

		float rating;
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
		}

		System.out.println("Digite quantos gêneros o filmes vai ter");
		int k = sc.nextInt();

		String[] genre = new String[k];
		sc.nextLine();
		for (int i = 0; i < k; i++) {
			System.out.println("Digite o " + (i + 1) + "º gênero + ENTER");
			genre[i] = sc.nextLine();
		}
		System.out.println("Digite a avaliação do filme, separado por vírgula");
		rating = sc.nextFloat();

		System.out.println("Digite o ano de lançamento do filme (YYYY)");
		int date = sc.nextInt();

		Movie filme = new Movie(false, id, title, date, certificado, genre, rating, director);
		return filme;

	}
}