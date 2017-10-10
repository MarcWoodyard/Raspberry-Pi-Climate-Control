public class Toogle {
  public static void main(String[] args) {

    Runtime runTime = Runtime.getRuntime();

    try {
        runTime.exec("gpio mode 100 pwm");
        runTime.exec("gpio pwm-ms");
        runTime.exec("gpio pwmc 192");
        runTime.exec("gpio pwmr 2000");
		    runTime.exec("gpio pwm 1 60");
		    Thread.sleep(600);
        runTime.exec("gpio pwm 1 55");
		    Thread.sleep(600);
		    runTime.exec("gpio pwm 1 60");
    } catch(Exception e) {
        e.printStackTrace();
    }
  }

}
