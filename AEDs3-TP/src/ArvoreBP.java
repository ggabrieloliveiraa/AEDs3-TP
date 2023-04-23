import java.util.*;
import java.io.*;

class Objeto {
	public int esq; // apontador esquerdo
	public int id;
	public int ponteiro; // ponteiro do id no arquivo de dados
	public int dir; // apontador direito

	Objeto(int e, int id, int ponteiro, int d) {
		this.id = id;
		this.ponteiro = ponteiro;
		this.esq = e;
		this.dir = d;
	}

	@Override
    public String toString() {
        return "Objeto [esq=" + esq + ", id=" + id + ", ponteiro=" + ponteiro + ", dir=" + dir + "]";
    }
}



class No {
	public int n; // quantidade de registros do no
	public No pai; // no pai
	public int posArvore; // posicao do no arquivo de indice b tree(isso nao vai ter no arquivo, mas vai
							// facilitar aqui pra ter controle)
	public ArrayList<Objeto> registros; // arrray de registros

	// construtor sem alterar a posicao
	/*
	 * No(int n, ArrayList<Objeto> r) { this.n = n;
	 * 
	 * this.registros = r; }
	 */

	No(int n, int posArv, ArrayList<Objeto> r) {
		this.n = n;
		this.posArvore = posArv;
		// tem que saber quando vai atualizar antes de usar esse construtor
		// nao sei como fazer isso direito

		this.registros = r;
	}

	@Override
    public String toString() {
		boolean senpai = false;
		if (pai != null){
			senpai = true; 
		}
        return "No [n=" + n + ", pai=" + senpai + ", posArvore=" + posArvore + ", registros=" + registros + "]";
    }
}

//acho que nao vamo usar
class NoFolha {
	public ArrayList<Objeto> registros;
	public int n;
	public int prox;
}

public class ArvoreBP {
	public No raiz;
	public int ordem = 8;
	public RandomAccessFile arvore;
	public int altura; // ter controle da altura vai ajudar em algumas operacoes

	ArvoreBP() throws Exception {
		this.arvore = new RandomAccessFile("../data/Arvore.bin", "rw");
		if (arvore.length() == 0){
			this.altura = 0;
			this.raiz = null; // quando a arvore e' criada a raiz comeca como null
			arvore.seek(0);
			arvore.writeInt(0);
			arvore.writeInt(8);
		} else {
			arvore.seek(0);
			this.altura = arvore.readInt();
			System.out.println(altura);
			int pos = arvore.readInt();
			System.out.println("posicao da raiz = " + pos);
			System.out.println("tamanho do arquivo = " + arvore.length());
			arvore.seek(pos);
			this.raiz = lerNo();
		}
		
	}

	/**
	 * Escreve um registro de objeto no arquivo da árvore.
	 * 
	 * @param registro o registro a ser adicionado no arquivo
	 * @throws Exception se ocorrer algum erro de escrita no arquivo
	 */
	public void escreverRegistro(Objeto registro, int i, int n) throws Exception {
		arvore.writeInt(registro.esq);
		arvore.writeInt(registro.id);
		arvore.writeInt(registro.ponteiro);
		if (i == n - 1) {
			arvore.writeInt(registro.dir);
		}
	}

	public void escreverRegistro(int i, int n) throws Exception {
		arvore.writeInt(-1);
		arvore.writeInt(-1);
		arvore.writeInt(-1);
		if (i == n) {
			arvore.writeInt(-1);
		}
	}

	/**
	 * Lê um registro de objeto do arquivo da árvore.
	 * 
	 * @return o registro lido do arquivo
	 * @throws Exception se ocorrer algum erro de leitura no arquivo
	 */
	public Objeto lerRegistro() throws Exception {
		int esq = arvore.readInt();
		int id = arvore.readInt();
		int ponteiro = arvore.readInt();
		int dir = arvore.readInt();
		Objeto registro = new Objeto(esq, id, ponteiro, dir);
		return registro;
	}

	/**
	 * Escreve um novo nó na árvore com um registro e aloca o espaço restante.
	 * 
	 * @param registro o registro a ser adicionado no nó
	 * @throws Exception se ocorrer algum erro de escrita no arquivo
	 */
	public void escreverNo(Objeto registro) throws Exception {
		// escreve um novo no com um registro e aloca o espaço restante
		arvore.seek(arvore.length());
		arvore.writeInt(1); // tamanho 1 inicialmente
		escreverRegistro(registro, 0, 1);
		// Escreve valores nulos nos espaços restantes do nó (6 registros)
		for (int i = 1; i < 7; i++) {
			arvore.writeInt(-1);
			arvore.writeInt(-1);
			arvore.writeInt(-1);
		}
	}

	/**
	 * Escreve os dados de um nó no arquivo da árvore.
	 * 
	 * @param no O nó cujos dados devem ser escritos no arquivo.
	 * @throws Exception Se ocorrer um erro ao escrever no arquivo da árvore.
	 */
	public void escreverNo(No no) throws Exception {
		arvore.seek(no.posArvore); // vai pra posicao do no na arvore
		arvore.writeInt(no.n); // escrever a quantidade de registro no nó
		/*primeiro teste*///System.out.println("no.n = " + no.n);
		/*primeiro teste*///System.out.println(no);

		if (no.n > 0){
			for (int i = 0; i < no.n; i++) {
				escreverRegistro(no.registros.get(i), i, no.n);
			}
		} else {
			for (int j = 0; j < ordem-1; j++){
				escreverRegistro(j, ordem-1);
			}
		}

	}

	/**
	 * Lê um nó da árvore a partir do arquivo de entrada.
	 * 
	 * @return o nó lido da árvore
	 * @throws Exception se ocorrer algum erro de leitura do arquivo
	 */
	public No lerNo() throws Exception {
		// Cria uma lista de objetos para armazenar os registros do nó
		ArrayList<Objeto> registros = new ArrayList<Objeto>();
		int posArv = (int) arvore.getFilePointer();
		/*primeiro teste*///System.out.println("posArv = " + posArv);
		No no = new No(-1, -1, registros);
		if (posArv != -1){
			int n = arvore.readInt(); // Lê o número de registros do nó
			//System.out.println("n = " + n);
			int dir = 0;
			int esq = 0;

			// Loop para ler cada registro do nó
			for (int i = 0; i < n; i++) {
				if (i == 0) { // Se for o primeiro registro, lê o ponteiro esquerdo da árvore
					esq = arvore.readInt();
				} else {
					esq = dir; // Se não for o primeiro, o ponteiro esquerdo é o direito do registro anterior
				}

				int id = arvore.readInt();
				int ponteiro = arvore.readInt();
				dir = arvore.readInt();

				// Cria um objeto com os dados do registro atual e adiciona na lista de
				// registros
				Objeto tmp = new Objeto(esq, id, ponteiro, dir);
				registros.add(tmp);
			}

			// Cria um novo nó com os registros lidos e retorna
			no.n = n;
			no.posArvore = posArv;
			no.registros = registros;
		} else {
			Objeto objTmp = new Objeto (-1, -1, -1, -1);
			No noTmp = new No(1, (int)arvore.length(), new ArrayList<Objeto>(Arrays.asList(objTmp)));
			escreverNo(noTmp);
			arvore.seek(posArv);
		}
		//System.out.println("LER NO = " + no);
		return no;
	}

	/**
	 * Escreve a raiz da árvore com um registro e aloca o espaço restante.
	 * 
	 * @param registro o registro a ser adicionado na raiz
	 * @throws Exception se ocorrer algum erro de escrita no arquivo
	 */
	public void escreverRaiz(Objeto registro) throws Exception {
		// Posiciona o cursor do arquivo na posição que indica o tamanho da raiz
		arvore.seek(4);
		arvore.writeInt(8);
		escreverNo(registro);
	}

	/**
	 * Aponta para o nó folha onde um registro com o ID especificado deve ser
	 * inserido, começando a busca pelo nó raiz e descendo na árvore até chegar à
	 * altura do nó folha.
	 *
	 * @param id      O ID do registro a ser inserido.
	 * @param posicao A posição do registro a ser inserido no arquivo de dados.
	 * @param no      O nó atual em que a busca deve começar.
	 * @throws Exception Se ocorrer um erro ao ler ou escrever no arquivo da árvore.
	 */
	public void apontarParaNoFolha(int id, int posicao, No no) throws Exception { // arrumar isso aqui
		int pos = 0;
		int alturaAtual = 0;
		boolean repete = false;

		// Enquanto a altura atual for menor que a altura da árvore menos 1 (que é a
		// altura do nó folha),
		// percorre a árvore descendendo na direção do nó folha.
		while (alturaAtual < this.altura - 1) {
			for (int i = 0; i < no.n; i++) {
				if (id < no.registros.get(i).id) {
					// a posição do próximo nó a ser visitado é o ponteiro esquerdo desse registro.
					pos = no.registros.get(i).esq;
					break;
				}
				if(id == no.registros.get(i).id){
					repete = true;
					break;
				}
			}
			if (id > no.registros.get(no.n - 1).id) {
				// a posição do próximo nó a ser visitado é o ponteiro direito do último
				// registro do nó.
				pos = no.registros.get(no.n - 1).dir;
			}
			if(id == no.registros.get(no.n - 1).id){
				repete = true;
				break;
			}

			// Lê o próximo nó a ser visitado e define seu pai como o nó atual.
			arvore.seek(pos);
			No proxNo = lerNo();
			proxNo.pai = no;
			alturaAtual++;
		}
		if (!repete){
			/*primeiro teste*///System.out.println("saindo do apontar para no folha");
			inserirNaFolha(id, posicao, no);
		}
	}

	/**
	 * Ordena os registros de uma folha com base no seu ID.
	 * 
	 * @param no O nó folha que será ordenado.
	 * @return O nó folha ordenado.
	 * @throws Exception Caso ocorra algum erro durante a ordenação.
	 */
	public No ordenarRegistros(No no) throws Exception {
		Comparator<Objeto> comparador = new Comparator<Objeto>() {
			@Override
			public int compare(Objeto o1, Objeto o2) {
				return Integer.compare(o1.id, o2.id);
			}
		};
		Collections.sort(no.registros, comparador); // ordena os registros da folha por id
		return no;

	}

	/**
	 * Insere um novo registro em um nó folha.
	 * 
	 * @param no       O nó folha onde o registro será inserido.
	 * @param registro O registro a ser inserido.
	 * @throws Exception Caso ocorra algum erro durante a inserção.
	 */
	public void inserirRegistroNo(No no, Objeto registro) throws Exception {
		no.registros.add(registro);
		no = ordenarRegistros(no);
		no.n++;
		/*primeiro teste*///System.out.println("aaaaaaaa");
		/*primeiro teste*///System.out.println("INSERIR REGISTRO NO = " + no);
		escreverNo(no);
	}

	/**
	 * Insere um novo registro em uma folha existente na árvore B.
	 * 
	 * @param id       O ID do novo registro a ser inserido.
	 * @param ponteiro O ponteiro para o novo registro a ser inserido.
	 * @param noPai    O nó pai que aponta para a folha onde o registro será
	 *                 inserido.
	 * @throws Exception Caso ocorra algum erro durante a inserção.
	 */
	public void inserirNaFolha(int id, int ponteiro, No noPai) throws Exception {
		int pos = 0;
		boolean repete = false;
		for (int i = 0; i < noPai.n; i++) {
			if (id < noPai.registros.get(i).id && id != -1) {
				pos = noPai.registros.get(i).esq;
				break;
			}
			if(id == noPai.registros.get(i).id){
				repete = true;
				break;
			}
		}
		if (id > noPai.registros.get(noPai.n - 1).id && id != -1) {
			pos = noPai.registros.get(noPai.n - 1).dir;
		}
		if(id == noPai.registros.get(noPai.n - 1).id){
			repete = true;
		}

		if (!repete){
			// Posiciona o ponteiro do arquivo da árvore B na folha onde o registro deve ser
			// inserido.
			/*primeiro teste*///System.out.println(pos);
			arvore.seek(pos);
			No noFolha = lerNo();
			noFolha.pai = noPai;
			Objeto registro = new Objeto(-1, id, ponteiro, -1);
			

			if (noFolha.n < 7) {
				inserirRegistroNo(noFolha, registro);
			} else {
				splitArvore(noFolha, registro); // se a folha tiver cheia
			}
		}
	}

	public void splitArvore(No no, Objeto registro) throws Exception {
		Objeto meio;
		//int alturaAtual = 0;
		int mediano = (ordem / 2) - 1; // calcula a posição do registro mediano
		if (no.pai == null) {
			mediano = 0;
			this.altura = this.altura++;
			/*primeiro teste*///System.out.println("ALSDJFLKASJFLTETUERJRKFLADFSJHK " + this.altura);
			arvore.seek(0);
			arvore.writeInt(this.altura);
		}

		// no.registros.add(registro); // adiciona o novo registro à folha
		// no = ordenarRegistros(no); // ordena os registros da folha

		No novoNo;
		meio = no.registros.get(mediano); // seleciona o registro mediano
			 if (no.pai != null && no.pai.n < ordem - 1) { // se a folha ainda não estiver cheia
				/*primeiro teste*///System.out.println("");

				// cria listas com os registros maiores e menores que o mediano
				ArrayList<Objeto> registrosMaiores = new ArrayList<Objeto>(no.registros.subList(mediano + 1, no.registros.size()));
				ArrayList<Objeto> registrosMenores = new ArrayList<Objeto>(no.registros.subList(0, mediano));

				/*primeiro teste*///System.out.println(no.n);
				// remove os registros da lista original que nao vao ser descartados
				for (int i = mediano; i < no.n; i++) {
					no.registros.remove(mediano);
				}

				// diminui o n do no que vai ficar na esquerda
				no.n = no.registros.size();

				// atualiza o número de registros do nó da direita
				novoNo = new No(registrosMaiores.size(), (int) arvore.length(), registrosMaiores);

				// atualiza os ponteiros do registro que subirá para o pai
				meio.esq = no.posArvore;
				meio.dir = novoNo.posArvore;

				novoNo.pai = no.pai;

				// verifica pra qual no vai o novo registro
				if (registro.id > meio.id) {
					novoNo.registros.add(registro);
					novoNo.n = novoNo.registros.size();
					novoNo = ordenarRegistros(novoNo);
				} else {
					no.registros.add(registro);
					no.n = no.registros.size();
					no = ordenarRegistros(no);
				}

				/*primeiro teste*///System.out.println("\n\nNO = " + no);
				/*primeiro teste*///System.out.println("\nNOVO NO = " + novoNo);
				
				// escreve os nós atualizados no arquivo
				escreverNo(no);
				escreverNo(novoNo);


				if (no.pai == null) {
					// nó atual é a raiz
						// se a raiz estiver cheia, faça o split recursivo no nó pai
						No novaRaiz = new No(1, (int) arvore.length(), new ArrayList<Objeto>(Arrays.asList(meio)));
						escreverRaiz(novaRaiz);
						//splitArvore(no, registro);
					
				} else if (no.pai.n < ordem - 1) {
					// se a raiz ainda não estiver cheia, basta adicionar o registro mediano
					no.pai.registros.add(meio);
					no.pai.n++;
					ordenarRegistros(no);
					/*primeiro teste*///System.out.println("\nNO PAI = " + novoNo);
					escreverNo(no);
				} else {
					// nó atual não é a raiz, continue a inserção normalmente
					splitArvore(no, meio);
				}

			}else {
				/*primeiro teste*///System.out.println("");

				// cria listas com os registros maiores e menores que o mediano
				ArrayList<Objeto> registrosMaiores = new ArrayList<Objeto>(no.registros.subList(mediano + 1, no.registros.size()));
				ArrayList<Objeto> registrosMenores = new ArrayList<Objeto>(no.registros.subList(0, mediano));

				/*primeiro teste*///System.out.println(no.n);
				// remove os registros da lista original que nao vao ser descartados
				for (int i = mediano; i < no.n; i++) {
					no.registros.remove(mediano);
				}

				// diminui o n do no que vai ficar na esquerda
				no.n = no.registros.size();

				// atualiza o número de registros do nó da direita
				novoNo = new No(registrosMaiores.size(), (int) arvore.length(), registrosMaiores);

				// atualiza os ponteiros do registro que subirá para o pai
				meio.esq = no.posArvore;
				meio.dir = novoNo.posArvore;

				novoNo.pai = no.pai;

				// verifica pra qual no vai o novo registro
				if (registro.id > meio.id) {
					novoNo.registros.add(registro);
					novoNo.n = novoNo.registros.size();
					novoNo = ordenarRegistros(novoNo);
				} else {
					no.registros.add(registro);
					no.n = no.registros.size();
					no = ordenarRegistros(no);
				}

				/*primeiro teste*///System.out.println("\n\nNO = " + no);
				/*primeiro teste*///System.out.println("\nNOVO NO = " + novoNo);
				
				// escreve os nós atualizados no arquivo
				escreverNo(no);
				escreverNo(novoNo);

				No novaRaiz = new No(1, (int) arvore.length(), new ArrayList<Objeto>(Arrays.asList(meio)));
				escreverRaiz(novaRaiz);
			}
			
	}

	public void escreverRaiz (No novaRaiz) throws Exception{
		Scanner sc = new Scanner (System.in);
		arvore.seek(0);
		arvore.writeInt(this.altura + 1);
		this.altura++;

		System.out.println("nova raiz = " + novaRaiz);
		/*System.out.println("tamanho do arquivo = " + arvore.length());
		System.out.println("pos raiz = " + novaRaiz.posArvore);
		System.out.println("n raiz = " + novaRaiz.n);*/
		//sc.nextLine();
		arvore.writeInt(novaRaiz.posArvore);
		novaRaiz.posArvore = novaRaiz.posArvore; // the vision
		
		this.raiz = novaRaiz;
		escreverNo (novaRaiz);
	}

	public void buscar(int id) throws Exception {
		arvore.seek(0);
		this.altura = arvore.readInt();
		/*primeiro teste*///System.out.println("altura = " + this.altura);
		int raizPos = arvore.readInt();
		arvore.seek(raizPos);

		No no = lerNo();

		int pos = 0;
		int alturaAtual = 0;
		for (int vt = 0; vt < no.n; vt++){
			/*primeiro teste*///System.out.println("RAIZ === " + no.registros.get(vt).id);
		}
		// Enquanto a altura atual for menor que a altura da árvore menos 1 (que é a
		// altura do nó folha),
		// percorre a árvore descendendo na direção do nó folha.
		while (alturaAtual < this.altura) {
			for (int vt = 0; vt < no.n; vt++){
				/*primeiro teste*///System.out.println("no === " + no.registros.get(vt).id);
			}
			for (int i = 0; i < no.n; i++) {
				if (id == no.registros.get(i).id){
					/*primeiro teste*///System.out.println("ENCONTRADOOOOOOOOOOOOO");
				}
				if (id < no.registros.get(i).id) {
					// a posição do próximo nó a ser visitado é o ponteiro esquerdo desse registro.
					pos = no.registros.get(i).esq;
					break;
				}
			}
			if (id > no.registros.get(no.n - 1).id) {
				// a posição do próximo nó a ser visitado é o ponteiro direito do último
				// registro do nó.
				pos = no.registros.get(no.n - 1).dir;
			}
			if (id == no.registros.get(no.n - 1).id){
				/*primeiro teste*///System.out.println("ENCONTRADOOOOOOOOOOOOO");
			}

			// Lê o próximo nó a ser visitado e define seu pai como o nó atual.
			arvore.seek(pos);
			No proxNo = lerNo();
			proxNo.pai = no;
			alturaAtual++;
			no = proxNo;
		}
		/*primeiro teste*///System.out.println("saiur");
	}

	public void iniciarRaiz (No novaRaiz) throws Exception{
		arvore.seek(0);
		arvore.writeInt(this.altura + 1);
		this.altura++;
		/*primeiro teste*///System.out.println("iniciiada altura = " + this.altura);
		arvore.writeInt((int)arvore.length() + 4);
		novaRaiz.posArvore = (int)arvore.length();
		arvore.seek(arvore.length());
		this.raiz = novaRaiz;
		escreverNo (novaRaiz.registros.get(0));
	}

	public void inserir(int id, int pos) throws Exception {
		Scanner sc = new Scanner (System.in);
		if (this.raiz == null) {
			/*primeiro teste*///System.out.println("aqui");
			Objeto registro = new Objeto(100, id, pos, 192);
			No novaRaiz = new No(1, 8, new ArrayList<Objeto>(Arrays.asList(registro)));
			iniciarRaiz(novaRaiz);
			No esq = new No(0, (int)arvore.length(), new ArrayList<Objeto>());
			escreverNo(esq);
			No dir = new No(0, (int)arvore.length(), new ArrayList<Objeto>());
			escreverNo(dir);
		} else {
			//sc.nextLine();
			/*primeiro teste*///System.out.println("altura ====== " + this.altura);
			apontarParaNoFolha(id, pos, this.raiz);
		}
	}

}