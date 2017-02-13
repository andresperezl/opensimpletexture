import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  private static final int WIDTH = 512;
  private static final int HEIGHT = 512;
  private static final int COLORS = 32;
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
  private static final ThreadFactory THREAD_FACTORY = new ThreadFactory(){

    public Thread newThread(Runnable r) {
      return new Thread(){
        @Override
        public void run() {
          try {
            generateImage();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      };
    }
  };


  public static void main(String[] args)
      throws IOException, ExecutionException, InterruptedException {
    int threads = 1;
    if(args.length > 0){
      threads = Integer.parseInt(args[0]);
    }
    ArrayList<Future> futures = new ArrayList<Future>();
    ExecutorService executorService = Executors.newFixedThreadPool(threads, THREAD_FACTORY);
    for(int i = 0; i < threads; i++)
      futures.add(executorService.submit(new Thread()));
    //for(Future f : futures) f.get();
  }

  private static ArrayList<Integer> generateColorArray(Random rnd) {

    ArrayList<Integer> colors = new ArrayList<Integer>(COLORS);
    for(int i = 0; i < COLORS; i++){
      int color;
      do{
        int red = 0x0000ff & rnd.nextInt(256);
        int green =   0x0000ff & rnd.nextInt(256);
        int blue = 0x0000ff & rnd.nextInt(256);
        color = (red << 16) | (green << 8) | blue;
        LOGGER.log(Level.INFO, "Color: " + Integer.toHexString(color) + "(" +
            red + "R" + green + "G" + blue + "B)");

      }while(colors.contains(color));
      colors.add(color);
    }
    Collections.sort(colors);
    return colors;
  }

  private static boolean generateImage() throws IOException {
     Random rnd = new Random(System.nanoTime());
    double featureSize = rnd.nextDouble() * 1024;
    ArrayList<Integer> colors = generateColorArray(rnd);
    OpenSimplexNoise noise = new OpenSimplexNoise(System.nanoTime());
    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        double value = noise.eval(x / featureSize, y / featureSize);
        int rgb = colors.get((int) ((value + 1) * (COLORS / 2.0)));
        image.setRGB(x, y, rgb);
      }
    }
    return ImageIO.write(image, "png", new File
        ("textures/noise" + System.nanoTime() + ".png"));
  }
}
