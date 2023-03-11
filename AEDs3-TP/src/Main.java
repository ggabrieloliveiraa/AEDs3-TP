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

public class Main {

	public static void main(String[] args) {
		interfac();
		try {
			OrdenacaoExterna.externalSort("../data/arquivo", 2517, 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void interfac() {
		Scanner scanner = new Scanner(System.in);
		try {
			CRUD crud = new CRUD("../data/arquivo.bin");

			while (true) {
				System.out.println("Selecione uma operação:");
				System.out.println("1 - Listar");
				System.out.println("2 - Inserir");
				System.out.println("3 - Atualizar");
				System.out.println("4 - Excluir");
				System.out.println("5 - Mostrar tudo");
				System.out.println("6 - Carga inicial");
				System.out.println("0 - Sair");

				int id = 0;
				Movie m_temp = new Movie();
				int opcao = scanner.nextInt();
				scanner.nextLine(); // limpa o buffer do scanner
				switch (opcao) {

					// listar
					case 1:
						System.out.println("1 - Buscar por ID");
						System.out.println("2 - Buscar por título");

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

						crud.inserir(m_temp.toByteArray());
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
						crud.mostrarTudo("arquivo.bin", 4);
						break;
					case 6:
						crud.cargaInicial();
						System.out.println("Carga inicial realizada!");
						System.out.println("Maior ID = " + crud.getMaxId());
						break;
					case 0:
						System.out.println("Saindo...");
						crud.fechar();
						return;
					default:
						System.out.println("Opção inválida!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// pegar cada atributo do filme por input do terminal
	public static Movie getMovie() {
		Scanner sc = new Scanner(System.in);

		String title, director;

		float rating;
		// java.util.Date year;
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
				// break;
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
		// Date year = new Date();// por enquanto deixei assim(atributos[1])
		// id = 10064;

		Movie filme = new Movie(false, id, title, date, certificado, genre, rating, director);

		return filme;

	}
}