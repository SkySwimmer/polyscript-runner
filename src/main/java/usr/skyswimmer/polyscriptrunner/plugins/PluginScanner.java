package usr.skyswimmer.polyscriptrunner.plugins;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.asf.cyan.fluid.bytecode.FluidClassPool;
import org.objectweb.asm.tree.ClassNode;

public class PluginScanner {

	private FluidClassPool pool;

	public PluginScanner(FluidClassPool pool) {
		this.pool = pool;
	}

	public String[] findAllPluginClassNames() {
		ArrayList<String> nodes = new ArrayList<String>();
		for (ClassNode node : pool.getLoadedClasses()) {
			if (nodeExtends(node, pool, IPolyscriptPlugin.class) && !Modifier.isAbstract(node.access)
					&& !Modifier.isInterface(node.access)) {
				nodes.add(node.name.replace("/", "."));
			}
		}
		return nodes.toArray(t -> new String[t]);
	}

	private boolean nodeExtends(ClassNode node, FluidClassPool pool, Class<?> target) {
		while (true) {
			// Check node
			if (node.name.equals(target.getTypeName().replace(".", "/")))
				return true;

			// Check interfaces
			if (node.interfaces != null) {
				for (String inter : node.interfaces) {
					try {
						if (nodeExtends(pool.getClassNode(inter), pool, target))
							return true;
					} catch (ClassNotFoundException e) {
					}
				}
			}

			// Check if end was reached
			if (node.superName == null || node.superName.equals("java/lang/Object"))
				break;
			try {
				node = pool.getClassNode(node.superName);
			} catch (ClassNotFoundException e) {
				break;
			}
		}
		return false;
	}

}
