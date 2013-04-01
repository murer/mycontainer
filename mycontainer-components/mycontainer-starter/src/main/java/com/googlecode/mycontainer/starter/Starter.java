package com.googlecode.mycontainer.starter;

public class Starter {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		if (args.length == 0 || args[0].equals("-h")
				|| args[0].equals("--help")) {
			printSintax();
			return;
		}

		String tmp = args[0].substring(1);
		String type = Character.toUpperCase(tmp.charAt(0)) + tmp.substring(1);
		Class<StarterType> clazz = (Class<StarterType>) Class
				.forName(Starter.class.getPackage().getName() + ".StarterType" + type);
		StarterType instance = clazz.newInstance();
		instance.execute(args);
	}

	private static void printSintax() {
		System.out.println("com.googlecode.mycontainer.starter.Starter -url [url]");
		System.out.println("com.googlecode.mycontainer.starter.Starter -file [path]");
		System.out
				.println("com.googlecode.mycontainer.starter.Starter -resource [resource in the classpath]");
	}
}
