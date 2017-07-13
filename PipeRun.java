import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PipeRun {
	private static class Reaper implements Runnable {
		final private InputStream inputStream;
		final private OutputStream outputStream;

		Reaper(InputStream inputStream, OutputStream outputStream) {
			this.inputStream = inputStream;
			this.outputStream = outputStream;
		}

		@Override
		public void run() {
			final byte buf[] = new byte[2048];
			int len;
			try {
				while((len = inputStream.read(buf)) != -1) {
					outputStream.write(buf, 0, len);
				}
				outputStream.close();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		final Process p = new ProcessBuilder(args).start();
		final Thread thOut = new Thread(new Reaper(p.getInputStream(), System.out));
		final Thread thErr = new Thread(new Reaper(p.getErrorStream(), System.err));
		thOut.start();
		thErr.start();
		new Thread(new Reaper(System.in, p.getOutputStream())).start();
		final int exit_code = p.waitFor();
		thOut.join();
		thErr.join();
		System.exit(exit_code);
	}
}
