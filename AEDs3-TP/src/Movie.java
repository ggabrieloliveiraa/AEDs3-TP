import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.text.*;

public class Movie {
	protected boolean lapide;
	protected int id;
	protected String title, director;
	protected String certificate;
	protected String[] genre;
	protected float rating;
	protected java.util.Date year;

	public Movie(boolean lapide, int id, String title, int year, String certificate, String[] genre, float rating,
			String director) {
		this.lapide = lapide;
		this.id = id;
		this.title = title;
		this.director = director;
		this.certificate = certificate;
		this.genre = genre;
		this.rating = rating;
		this.year = parseDate(year);
	}

	public static Date parseDate(int date) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + date); // só temos informação do ano, logo todas
																				// as datas serão 1 de janeiro
		} catch (ParseException e) {
			return null;
		}
	}

	public Movie() {
		this.id = -1;
		this.title = "";
		this.director = "";
		this.certificate = "";
		this.genre = "a,b".split(",");
		this.rating = 0F;
		this.year = parseDate(2005);
	}

	@Override
	public String toString() {
		return "Movie [lapide=" + lapide + ", id=" + id + ", title=" + title + ", director=" + director
				+ ", certificate=" + certificate + ", genre=" + Arrays.toString(genre) + ", rating=" + rating
				+ ", year=" + year + "]";
	}

	public byte[] toByteArray() throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		dos.writeBoolean(lapide);
		dos.writeInt(id);
		dos.writeUTF(title);
		dos.writeUTF(director);
		dos.writeBytes(String.format("%-9s", certificate)); // escreve a string com tamanho fixo de 5 caracteres
		dos.writeInt(genre.length);
		String stringzona = "";
		for (int i = 0; i < genre.length; i++) {
			stringzona += genre[i];
			stringzona += ",";
		}
		dos.writeUTF(stringzona);
		stringzona = stringzona.substring(0, stringzona.length() - 1);
		dos.writeFloat(rating);
		dos.writeLong(year.getTime());

		return baos.toByteArray();
	}

	public void fromByteArray(byte ba[]) throws IOException {

		ByteArrayInputStream bais = new ByteArrayInputStream(ba);
		DataInputStream dis = new DataInputStream(bais);

		lapide = dis.readBoolean();
		id = dis.readInt();
		//System.out.println("id = " + id);
		title = dis.readUTF();
		director = dis.readUTF();
		byte[] stringBytes = new byte[9];
		dis.readFully(stringBytes);
		certificate = new String(stringBytes);
		dis.readInt();
		String allGen = dis.readUTF();
		genre = allGen.split(",");
		rating = dis.readFloat();
		year = new Date(dis.readLong());


	}
}