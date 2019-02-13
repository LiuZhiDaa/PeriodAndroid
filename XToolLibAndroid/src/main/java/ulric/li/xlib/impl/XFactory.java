package ulric.li.xlib.impl;

import java.util.Map;

import ulric.li.xlib.intf.IXFactory;
import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObject;

public abstract class XFactory implements IXFactory {
    @Override
    public IXObject createInstance(Class<?> classInterface) {
        return createInstance(classInterface, null);
    }

    @Override
    public IXObject createInstance(Class<?> classInterface, Class<?> classImplement) {
        if (null == classInterface || !IXObject.class.isAssignableFrom(classInterface))
            return null;

        XFactoryImplementMap xFactoryImplementMap = findImplementMap(classInterface);
        if (null == xFactoryImplementMap)
            return null;

        int nImplementPos = findImplementPos(xFactoryImplementMap, classImplement);
        if (-1 == nImplementPos)
            return null;

        IXObject iXObject = null;
        if (IXManager.class.isAssignableFrom(classInterface)) {
            synchronized (XFactory.class) {
                if (null != xFactoryImplementMap.mArrayIXObject[nImplementPos]) {
                    iXObject = xFactoryImplementMap.mArrayIXObject[nImplementPos];
                } else {
                    try {
                        iXObject = (IXObject) xFactoryImplementMap.mArrayClassImplement[nImplementPos].newInstance();
                        xFactoryImplementMap.mArrayIXObject[nImplementPos] = iXObject;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            synchronized (XFactory.class) {
                try {
                    iXObject = (IXObject) xFactoryImplementMap.mArrayClassImplement[nImplementPos].newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return iXObject;
    }

    @Override
    public boolean isClassInterfaceExist(Class<?> classInterface) {
        if (null == classInterface)
            return false;

        return mXFactoryInterfaceMap.containsKey(classInterface);
    }

    private XFactoryImplementMap findImplementMap(Class<?> classInterface) {
        if (null == classInterface)
            return null;

        return mXFactoryInterfaceMap.get(classInterface);
    }

    private int findImplementPos(XFactoryImplementMap xFactoryImplementMap, Class<?> classImplement) {
        if (null == xFactoryImplementMap || null == xFactoryImplementMap.mArrayClassImplement || 0 == xFactoryImplementMap.mArrayClassImplement.length)
            return -1;

        if (null == classImplement)
            return 0;

        for (int nIndex = 0; nIndex < xFactoryImplementMap.mArrayClassImplement.length; nIndex++) {
            if (classImplement == xFactoryImplementMap.mArrayClassImplement[nIndex]) {
                return nIndex;
            }
        }

        return -1;
    }

    protected Map<Class<?>, XFactoryImplementMap> mXFactoryInterfaceMap = null;

    protected class XFactoryImplementMap {
        public Class<?>[] mArrayClassImplement = null;
        public IXObject[] mArrayIXObject = null;

        public XFactoryImplementMap(Class<?>[] arrayClassImplement, IXObject[] arrayIXObject) {
            this.mArrayClassImplement = arrayClassImplement;
            this.mArrayIXObject = arrayIXObject;
        }
    }
}
