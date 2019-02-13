package ulric.li.xlib.intf;

public interface IXFactory {
    IXObject createInstance(Class<?> classInterface);

    IXObject createInstance(Class<?> classInterface, Class<?> classImplement);

    boolean isClassInterfaceExist(Class<?> classInterface);
}
