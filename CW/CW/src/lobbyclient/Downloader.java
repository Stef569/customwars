package lobbyclient;
import java.net.*;
import java.io.*;

public class Downloader 
{
		public String dataout = "";

	   public Downloader()
	   {
		   /*
	      if (args.length == 0) {
	         System.out.println("Please specify a URL on the command line.");
	         return;
	      }*/
	      try {
	         readTextFromURL("http://battle.customwars.com/list.pl");
	      }
	      catch (Exception e) {
	         System.out.println("\n*** Sorry, an error has occurred ***\n");
	         System.out.println(e);
	      }  
	      //EasyWriter writer = new EasyWriter("gamelist.dat");
	      //writer.print(dataout);
	      //writer.close();
	   }
	   
	   void readTextFromURL( String urlString ) throws Exception {
	        // This subroutine attempts to copy text from the
	        // specified URL onto the screen.  All errors must
	        // be handled by the caller of this subroutine.
	        
	      /* Open a connection to the URL, and get an input stream
	         for reading data from the URL. */
		   EasyWriter writer = new EasyWriter("gamelist.dat");
		   
	      URL url = new URL(urlString);
	      URLConnection connection = url.openConnection();
	      InputStream urlData = connection.getInputStream();

	      /* Check that the content is some type of text.  Note: If
	         getContentType() method were called before getting 
	         the input stream, it is possible for contentType to be
	         null only because no connection can be made.  The
	         getInputStream() method will throw an error if no
	         connection can be made. */

	      String contentType = connection.getContentType();
	      if (contentType == null || contentType.startsWith("text") == false)
	         throw new Exception("URL does not refer to a text file.");
	         
	      /* Copy characters from the input stream to the screen, until
	         end-of-file is encountered  (or an error occurs). */

	      while (true) {
	         int data = urlData.read();
	         if (data < 0)
	            break;
	         writer.print((char)data);
	         //System.out.print((char)data);
	      }
	      writer.close();
	      //System.out.println(dataout);
	      
	   } // end readTextFromURL()

}
