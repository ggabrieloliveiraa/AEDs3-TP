import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.DecimalFormat;

public class Movie {
	protected boolean lapide;
	protected int id;
	protected String title, director;
	protected String certificate;
	protected String[] genre;
	protected float rating;
	protected java.util.Date year;

	public Movie(boolean lapide, int id, String title, Date year, String certificate, String[] genre, float rating, String director) {
		this.lapide = lapide;
		this.id = id;
		this.title = title;
		this.director = director;
		this.certificate = certificate;
		this.genre = genre;
		this.rating = rating;
		this.year = year;
	}

	public Movie() {
		this.id = -1;
		this.title = "";
		this.director = "";
		this.certificate = "";
		this.genre = null;
		this.rating = 0F;
		this.year = null;
	}
	


	@Override
	public String toString() {
		return "Movie [id=" + id + ", title=" + title + ", director=" + director
				+ ", certificate=" + certificate + ", genre=" + Arrays.toString(genre) + ", rating=" + rating
				+ ", year=" + year + "]";
	}

	public byte[] toByteArray() throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		dos.writeInt(id);
		dos.writeUTF(title);
		dos.writeUTF(director);
		dos.writeUTF(certificate);
		dos.writeUTF(genre.toString());
		dos.writeFloat(rating);
		dos.writeLong(year.getTime());
		
		return baos.toByteArray();
	}

	public void fromByteArray(byte ba[]) throws IOException {

		ByteArrayInputStream bais = new ByteArrayInputStream(ba);
		DataInputStream dis = new DataInputStream(bais);

		id = dis.readInt();
		title = dis.readUTF();
		director = dis.readUTF();
		certificate = dis.readUTF();
		genre = dis.readUTF().split(",");
		rating = dis.readFloat();
		year = new Date(dis.readLong());

	}
}