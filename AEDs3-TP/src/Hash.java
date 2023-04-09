import java.io.RandomAccessFile;
import java.util.*;

class Registro {
	public int id;
	public boolean lapide;
	public int pos;

	public Registro(int id, boolean lapide, int pos) {
		this.id = id;
		this.lapide = lapide;
		this.pos = pos;
	}
}

public class Hash {
	private RandomAccessFile dir;
	private RandomAccessFile bucket;
	private int profundidade;
	private int p;
	private int bucketSize = 504;

	public Hash(int profundidade) throws Exception {
		this.dir = new RandomAccessFile("dir.bin", "rw");
		this.bucket = new RandomAccessFile("bucket.bin", "rw");
		this.profundidade = profundidade;
		this.p = (int) Math.pow(2, profundidade);
		// this.buckets = new ArrayList[p];
		// this.diretorio = new int[p];
		dir.writeInt(profundidade); // profundidade do diretorio no cabeçalho do arquivo
		for (int i = 0; i < p; i++) {
			int endereco = i * ((bucketSize * 9) + 8);
			dir.writeInt(endereco); // escreve os endereços de onde começam os buckets no arquivo
									// de
									// indice
			bucket.seek(endereco);
			System.out.println(" e = " + endereco);
			bucket.writeInt(profundidade); // profundiade do diretorio = profundidade do bucket inicialmente
			bucket.writeInt(0); // tamanho 0
		}
	}

	public void inserir(int id, boolean lapide, int pos) throws Exception {
		Scanner sc = new Scanner(System.in);
		// RandomAccessFile bucket = new RandomAccessFile("bucket.bin", "rw");
		// RandomAccessFile dir = new RandomAccessFile("dir.bin", "rw");
		// System.out.println("PROFUNDIADE = " + profundidade);
		// System.out.println("this.p = " + this.p);
		// System.out.println("id = " + id);
		int hash = hash(id);
		int dirPesq = 4 + (4 * hash);
		System.out.println("disPesq = " + dirPesq);
		dir.seek(dirPesq); // vai pra posicao onde tem o endereco que aponta pro bucket(cabeçalho + 4 *
								// tamanho endereço)
		int bucketAdress = dir.readInt(); // ler o endereço que aponta pro bucket que quer
		// System.out.println("ba = " + bucketAdress);
		bucket.seek(bucketAdress);
		int pBucket = bucket.readInt();
		// System.out.println("pbucket = " + pBucket);
		int tamanho = bucket.readInt();
		// System.out.println("tb = " + tamanho);
		if (tamanho < 504) {
			System.out.println("ba = " + bucketAdress);
			System.out.println("tamanho = " + tamanho);

			System.out.println(id);
			bucket.seek(bucket.getFilePointer() + (tamanho * 9));
			bucket.writeInt(id);
			bucket.writeBoolean(false); // lapide
			bucket.writeInt(pos);
			bucket.seek(bucketAdress + 4);
			tamanho++;
			bucket.writeInt(tamanho);
		} else if (pBucket == profundidade) {
			System.out.println("pbucket == profundidade");
			// System.out.println("ba = " + bucketAdress);
			// se profundidade local for igual a profundidade global, aumenta o diretorio
			bucket.seek(bucket.getFilePointer() - 8);
			pBucket++;
			bucket.writeInt(pBucket); // nova profundidade local
			bucket.writeInt(0); // tamanho passa a ser 0 (vai ser redistribuido)
			int bucketPos = (int) bucket.getFilePointer();

			aumentarDir(hash); // aumentar diretorio
			int lastB = getLastBucket();
			int newLastB = lastB + 8 + (9 * bucketSize);
			bucket.seek(newLastB);
			bucket.writeInt(pBucket);
			bucket.writeInt(0);
			redistribuir(bucketPos);
			inserir(id, false, pos);
		} else {
			System.out.println("!!!!!!!!!!!!!!!!");
			//sc.next();
			// se profundidade local for menor que a profundidade global(nao precisa criar
			// um novo diretorio)
			bucket.seek(bucket.getFilePointer() - 8);
			pBucket++;
			bucket.writeInt(pBucket); // nova profundidade local
			bucket.writeInt(0);//zerando o bucket

			int bucketPos = (int) bucket.getFilePointer();
			int pAntigo = (int) Math.pow(2, (profundidade - 1));
			// PROBLEMA AQUI!
			//dir.seek(dir.length() - 4);
			System.out.println("hash = " + hash);
			int lastB = getLastBucket();
			System.out.println("lastB = " + lastB);
			int newLastB = lastB + 8 + (9 * bucketSize);
			int maiorDir = (int)dir.length() - 4;
			System.out.println("pr = " + p);
			System.out.println("maiorDir = " + maiorDir);
			int dirAtual = 4 + 4 * hash;
			int dirNovo = dirAtual + pAntigo * 4;
			System.out.println("dir atual = " + dirAtual); //PROBLEMA POR AQUI!!!
			dir.seek(dirNovo); // ir para ponteiro que vai apontar para o novo bucket(!!!)
			System.out.println("dir novo = " + dirNovo);
			// long bL = bucket.length();
			// System.out.println("bl = " + bL);
			// System.out.println("bl(int = " + (int)bL);
			System.out.println("newLastB = " + newLastB);
			System.out.println("p = " + bucketAdress);
			System.out.println("dirp = " + dir.getFilePointer());
			int posAntiga = dir.readInt();
			System.out.println("pdir = " + posAntiga);
			dir.seek(dirNovo);
			dir.writeInt(newLastB); // tava bucket.length aqui antes, errado pq o ultimo bucket pode nao estar cheio
			//dir.getFD().sync();
			dir.seek(dir.getFilePointer() - 4);
			int wtf = dir.readInt();
			System.out.println("wtf??? = " + wtf);
			bucket.seek(newLastB);
			bucket.writeInt(pBucket);
			bucket.writeInt(0);
			System.out.println("tchauuu");
			redistribuir(bucketPos);
			//sc.next();

			inserir(id, false, pos); //AQUI TA FICANDO INFINITO!!!
			System.out.println("oieeee");
		}
	}
	public int getLastBucket () throws Exception{
		dir.seek(4);
		int b = 0;
		int maior = 0; // valor inicial definido como zero
		for (int i = 0; i < p; i++) {
		    b = dir.readInt();
		    if (b > maior) {
		        maior = b;
		    }
		}
		System.out.println("m = " + maior);
		return maior;
	}

	public void aumentarDir(int hash) throws Exception {
		// RandomAccessFile dir = new RandomAccessFile("dir.bin", "rw");
		this.profundidade++;
		dir.seek(0);
		dir.writeInt(profundidade);
		int pNovo = (int) Math.pow(2, profundidade);
		int[] ponteiros = new int[p];
		dir.seek(4);
		for (int i = 0; i < p; i++) { // armazena os ponteiros antigos
			ponteiros[i] = dir.readInt();
		}
		dir.seek(4 + 4 * p); // pula para onde começa a ter os novos ponteiros (dir.length talvez tb funcione?)
		int j = 0;
		for (int i = p; i < pNovo; i++) { // escreve os novos ponteiros
			System.out.println(dir.getFilePointer());
			dir.writeInt(ponteiros[j]);
			j++;
		}
		dir.seek(4 + (hash * 4 + p * 4)); // ir para ponteiro que vai apontar para o novo bucket
		dir.writeInt(getLastBucket() + ((bucketSize * 9) + 8)); // escrever a nova posicao(ponteiro do ultimo
																	// bucket + tamanho de um bucket)
		//ponteiros[j-1] nao necessariamente o ultimo endereco
		this.p = pNovo;
	}

	public void redistribuir(int pos) throws Exception {
		// RandomAccessFile bucket = new RandomAccessFile("bucket.bin", "rw");
		bucket.seek(pos);
		Registro[] registros = new Registro[bucketSize];
		for (int i = 0; i < bucketSize; i++) { // salva os registros do bucket antigo que sera redistribuido em um
												// array de registros
			int idR = bucket.readInt();
			boolean lapideR = bucket.readBoolean();
			int posicaoR = bucket.readInt();
			registros[i] = new Registro(idR, lapideR, posicaoR);
		}
		for (int i = 0; i < bucketSize; i++) { // reinsere os registros com o novo diretorio
			// System.out.println("id gay = " + registros[i].id);
			inserir(registros[i].id, registros[i].lapide, registros[i].pos);
		}
		/*
		for (int i = 0; i < bucketSize; i++) { // reinsere os registros com o novo diretorio
			int hash = hash(registros[i].id);
			dir.seek(4 + 4 * hash); // vai pra posicao onde tem o endereco que aponta pro bucket(cabeçalho + 4 *
									// tamanho endereço)
			// System.out.println("haxixe uvinha = " + hash);
			int bucketAdress = dir.readInt(); // ler o endereço que aponta pro bucket que quer
			// System.out.println("ba = " + bucketAdress);
			bucket.seek(bucketAdress);
			bucket.readInt();
			// System.out.println("pbucket = " + pBucket);
			int tamanho = bucket.readInt();
			// System.out.println("tb = " + tamanho);
			// System.out.println(id);
			bucket.seek(bucket.getFilePointer() + (tamanho * 9));
			bucket.writeInt(registros[i].id);
			bucket.writeBoolean(registros[i].lapide); // lapide
			bucket.writeInt(registros[i].pos);
			bucket.seek(bucketAdress + 4);
			tamanho++;
			bucket.writeInt(tamanho);

		}
		*/
	}

	public int buscar(int id) throws Exception {
		// RandomAccessFile bucket = new RandomAccessFile("bucket.bin", "rw");
		// RandomAccessFile dir = new RandomAccessFile("dir.bin", "rw");
		int hash = hash(id);
		dir.seek(4 + hash * 4);
		int posBucket = dir.readInt();
		bucket.seek(posBucket + 4);
		int tamanho = bucket.readInt();
		System.out.println("t = " + tamanho);
		for (int i = 0; i < tamanho; i++) {
			int bucketId = bucket.readInt();
			System.out.println("bi = " + bucketId);
			boolean lapide = bucket.readBoolean();
			int pos = bucket.readInt();
			System.out.println("posbucket = " + pos);
			if (bucketId == id && lapide == false) {
				return pos;
			}
		}
		return -1;
	}

	private int hash(int k) throws Exception {
		// RandomAccessFile dir = new RandomAccessFile("dir.bin", "rw");
		dir.seek(0);
		int profundidade = dir.readInt();
		int p = (int) Math.pow(2, profundidade);
		return (k % p);
	}
}