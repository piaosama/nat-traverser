/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.sics.gvod.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Subclasses must have the following structure: (1) located in a package with
 * the name "se.sics.gvod.config" (2) all fields in the subclass should be
 * package-protected (or public) - not private (3) It must have a no-argument
 * constructor as its first constructor (4) It must have a public constructor
 * that takes *all* fields as its second constructor.This constructor can
 * optionally include all fields from this base class as the first parameters,
 * in the order in which they are declared in this class, then subsequently
 * fields from the subclass in the order in which they are declared.
 *
 * Subclasses should also have a public static build() method that returns a
 * default object. Setter methods are chainable, as they return an instance of
 * the subclass.
 *
 * @author jdowling
 */
public class AbstractConfiguration<T extends AbstractConfiguration> {

    private static AbstractConfiguration instance = null;
    protected int seed;

    protected AbstractConfiguration() {
        this.seed = BaseCommandLineConfig.getSeed();
    }

    public File store() throws IOException {
        Properties p = new Properties();
        p.setProperty("seed", "" + seed);
        for (Field f : getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                p.setProperty(f.getName(), "" + f.get(this));
            } catch (IllegalAccessException ex) {
                Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException("IllegalAccessException - " + ex.getMessage());
            }
        }
        File file = File.createTempFile(getClass().getCanonicalName(), "kompics");
        Writer writer = new FileWriter(file);
        p.store(writer, getClass().getCanonicalName());
        System.setProperty(getClass().getCanonicalName(), file.getCanonicalPath());
        return file;
    }

    public T loadP(Class<? extends AbstractConfiguration> t, String file)
            throws IOException {
        Properties p = new Properties();
        Reader reader = new FileReader(file);
        p.load(reader);
        String seedStr = p.getProperty("seed");
        int loadedSeed = Integer.parseInt(seedStr);
        try {
            Constructor<T> c = (Constructor<T>) t.getConstructor(new Class[]{});
            T obj = c.newInstance();
            for (Field f : getClass().getDeclaredFields()) {
                f.setAccessible(true);
                String val = p.getProperty(f.getName());
                if (val == null) {
                    throw new NullPointerException("In class " + getClass() + " "
                            + f.getName() + " was null.");
                }

                Class<?> pt = f.getType();
                if (pt.getSimpleName().compareTo("int") == 0) {
                    f.set(obj, Integer.parseInt(val));
                } else if (pt.getSimpleName().compareTo("long") == 0) {
                    f.set(obj, Long.parseLong(val));
                } else if (pt.getSimpleName().compareTo("boolean") == 0) {
                    f.set(obj, Boolean.parseBoolean(val));
                } else if (pt.getSimpleName().compareTo("double") == 0) {
                    f.set(obj, Double.parseDouble(val));
                } else if (pt.getSimpleName().compareTo("String") == 0) {
                    f.set(obj, val); 
                } else {
                    throw new IllegalStateException("Couldn't find parameter type to decode: " + f.getName());
                }
            }

            obj.setSeed(loadedSeed);
            return obj;
        } catch (InstantiationException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(t.getCanonicalName() + " - " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(t.getCanonicalName() + " - " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(t.getCanonicalName() + " - " + ex.getMessage());
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(t.getCanonicalName() + " - " + ex.getMessage());
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(t.getCanonicalName() + " - " + ex.getMessage());
        } catch (SecurityException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(t.getCanonicalName() + " - " + ex.getMessage());
        }
    }

    public static AbstractConfiguration load(Class<? extends AbstractConfiguration> type)
            throws IOException {
        try {
            Constructor<? extends AbstractConfiguration> c = type.getConstructor();
            instance = c.newInstance();
            AbstractConfiguration ac = instance.loadP(type,
                    System.getProperty(type.getCanonicalName()));
            if (ac == null) {
                throw new NullPointerException("Could not load configuration successfully: "
                        + type.getCanonicalName());
            }
            return ac;
        } catch (InstantiationException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } catch (SecurityException ex) {
            Logger.getLogger(AbstractConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        }
    }

    public int getSeed() {
        return seed;
    }

    public T setSeed(int seed) {
        this.seed = seed;
        return (T) this;
    }
}
