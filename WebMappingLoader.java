import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import jp.sample.ano.SampleAnotetion;

import java.util.List;
import java.util.ArrayList;

import java.util.Enumeration;
import java.net.URL;
import java.io.File;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.jar.JarFile;
import java.net.JarURLConnection;
import java.util.Collections;
import java.io.IOException;

public class WebMappingLoader {
	public static void main(String[] args){
		try{
			System.out.println("[WebMappingLoader Start]");
			WebMappingLoader my = new WebMappingLoader();
			//my.printMethodAnnotationInfo(jp.sample.AnoSample.class, SampleAnotetion.class);
			System.out.printf("%s,%s,%s,%s,%s,%s", "debug", "url", "package", "class", "method", "args");
			System.out.println("");
			my.printMethodAnnotationInfo("jp.sample", SampleAnotetion.class);

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			System.out.println("[WebMappingLoader End]");
		}
	}
	
	public void printMethodAnnotationInfo(String packageName, Class type){
		try{
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> e = cl.getResources(packageName.replace(".", "/"));
			List<Class> classes = new ArrayList();
			
			for (; e.hasMoreElements();) {
				URL url = e.nextElement();
				
				if ("file".equals(url.getProtocol())){
					File[] files = new File(url.getFile()).listFiles((dir, name) -> name.endsWith(".class"));
					classes = Arrays.asList(files).stream()
					.map(file -> file.getName())
					.map(name -> name.replaceAll(".class$", ""))
					.map(name -> packageName + "." + name)
					.map(fullName ->  classForName(fullName))
					.collect(Collectors.toList());
				}

				if ("jar".equals(url.getProtocol())){
					try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()) {
						String resourceName = packageName.replace('.', '/');
						classes = Collections.list(jarFile.entries()).stream()
							.map(jarEntry -> jarEntry.getName())
							.filter(name -> name.startsWith(resourceName))
							.filter(name -> name.endsWith(".class"))
							.map(name -> name.replace('/', '.').replaceAll(".class$", ""))
							.map(fullName -> classForName(fullName))
							.collect(Collectors.toList());
					} catch (IOException ioe) {
					}
				}
				
				for(Class clazz:classes){
					if (clazz != null){
						printMethodAnnotationInfo(clazz, type);
					}
				}
			}
	
			/*
			Package pack = Package.getPackage(packageName);
			
			Reflections reflections = new Reflections(packageName);
			
			Package[] packs = pack.getPackages();
			for(Package p:packs){
				printMethodAnnotationInfo(p.getName(), type);
			}
			*/
			
		}catch(Exception e){
			System.out.println("package:[" + packageName + "] is not exist.");
		}
	}
	
	private Class classForName(String fullName) {
		try{
			return Class.forName(fullName);
		}catch(Exception e){
			System.out.println("[" + fullName + "] is not exist." );
		}
		return null;
	}
	
	public void printMethodAnnotationInfo(Class clazz, Class type){
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method:methods){
			String packageName = clazz.getPackage().getName();
			printMethodAnnotationInfo(method, type, packageName, clazz.getName().replace(packageName + ".", ""));
		}
	}
	
	public void printMethodAnnotationInfo(Method method, Class type, String packageName, String className){
		Annotation[] annotations = method.getDeclaredAnnotationsByType(type);
		for(Annotation annotation:annotations){
			printMethodAnnotationInfo(annotation,
				                      packageName,
				                      className,
				                      method.getName(),
				                      getTypesToStrings(method.getTypeParameters()));
		}
	}

	//protected String[] getTypesToStrings(TypeVariable<Method>[] varTypes){
	protected String getTypesToStrings(TypeVariable<Method>[] varTypes){
		//List<String> varNameList = new ArrayList();
		String results = "";
		for(TypeVariable<Method> varType:varTypes){
			//varNameList.add(varType.getName());
			if (!results.equals("")){
				results = results + ",";
			}
			results = results + varType.getName();
		}
		//String[] results = new String[varNameList.size()];
		//varNameList.toArray(results);
		//return results;
		return results;
	}
	
	//public void printMethodAnnotationInfo(Annotation annotation, String methodName, String[] argsTypes){
	public void printMethodAnnotationInfo(Annotation annotation, String packageName, String className, String methodName, String argsTypes){
		String annotation2String = annotation.toString();
		String annotationValue = annotation2String.replace("@" + annotation.annotationType().getName(), "");
		annotationValue = annotationValue.replace("(value=","");
		annotationValue = annotationValue.replace(")", "");
		System.out.printf("[%s],%s,%s,%s,%s,(%s)", annotation2String, annotationValue, packageName, className, methodName, argsTypes);
		System.out.println("");
	}
	
}