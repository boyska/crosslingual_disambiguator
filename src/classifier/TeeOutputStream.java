package classifier;

import java.io.* ;


public class   TeeOutputStream
extends OutputStream
{
	OutputStream tee = null, out = null;


	public TeeOutputStream(OutputStream chainedStream, 
			OutputStream teeStream)
	{
		out = chainedStream;

		if (teeStream == null)
			tee = System.out;
		else
			tee = teeStream;
	}


	/**
	 * Implementation for parent's abstract write method.  
	 * This writes out the passed in character to the both,
	 * the chained stream and "tee" stream.
	 */

	public void write(int c) throws IOException
	{
		out.write(c);

		tee.write(c);
		tee.flush();
	}


	/**
	 * Closes both, chained and tee, streams.
	 */
	public void close() throws IOException
	{
		flush();

		out.close();
		tee.close();
	}


	/**
	 * Flushes chained stream; the tee stream is flushed 
	 * each time a character is written to it.
	 */
	public void flush() throws IOException
	{
		out.flush();
	}



	/** Test driver */
	public static void main(
			String args[]) throws Exception
			{
		FileOutputStream fos =
			new FileOutputStream("test.out");
		TeeOutputStream  tos =  
			new TeeOutputStream(fos, System.out);
		PrintWriter      pw  =  
			new PrintWriter(new OutputStreamWriter(tos));

		pw.println("Testing line 1");
		pw.println("Testing line 2");

		pw.close();
			}
}
