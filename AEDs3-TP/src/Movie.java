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

	protected int id, metascore;
	protected String title, director;
	protected String certificate;
	protected String[] genre;
	protected float rating;
	protected java.util.Date year;

	public Movie(String title, Date year, String certificate, String[] genre, float rating, int metascore, String director) {
		this.id = 0;
		this.metascore = metascore;
		this.title = title;
		this.director = director;
		this.certificate = certificate;
		this.genre = genre;
		this.rating = rating;
		this.year = year;
	}

	public Movie() {
		this.id = -1;
		this.metascore = -1;
		this.title = "";
		this.director = "";
		this.certificate = "";
		this.genre = null;
		this.rating = 0F;
		this.year = null;
	}
	


	@Override
	public String toString() {
		return "Movie [id=" + id + ", metascore=" + metascore + ", title=" + title + ", director=" + director
				+ ", certificate=" + certificate + ", genre=" + Arrays.toString(genre) + ", rating=" + rating
				+ ", year=" + year + "]";
	}

	public byte[] toByteArray() throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		dos.writeInt(id);
		dos.writeInt(metascore);
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
		metascore = dis.readInt();
		title = dis.readUTF();
		director = dis.readUTF();
		certificate = dis.readUTF();
		genre = dis.readUTF().split(",");
		rating = dis.readFloat();
		year = new Date(dis.readLong());

	}
}