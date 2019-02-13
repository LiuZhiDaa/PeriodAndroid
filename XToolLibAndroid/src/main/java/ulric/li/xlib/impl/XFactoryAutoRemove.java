package ulric.li.xlib.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ulric.li.xlib.intf.IXFactory;
import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObject;

public class XFactoryAutoRemove implements IXFactory {
    @Override
    public IXObject createInstance(Class<?> classInterface) {
        return createInstance(classInterface, null);
    }

    @Override
    public IXObject createInstance(Class<?> classInterface,
            Class<?> classImplement) {
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
                if (null != xFactoryImplementMap.mListIXObject.get(nImplementPos).get()) {
                    iXObject = xFactoryImplementMap.mListIXObject.get(nImplementPos).get();
                } else {
                    try {
                        iXObject = (IXObject) xFactoryImplementMap.mArrayClassImplement[nImplementPos]
                                .newInstance();
                        WeakReference<IXObject> wr = new WeakReference<IXObject>(iXObject);
                        xFactoryImplementMap.mListIXObject.set(nImplementPos, wr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            synchronized (XFactory.class) {
                try {
                    iXObject = (IXObject) xFactoryImplementMap.mArrayClassImplement[nImplementPos]
                            .newInstance();
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
        if (null == xFactoryImplementMap || null == xFactoryImplementMap.mArrayClassImplement
                || 0 == xFactoryImplementMap.mArrayClassImplement.length)
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
        public List<WeakReference<IXObject>> mListIXObject = null;

        public XFactoryImplementMap(Class<?>[] arrayClassImplement, IXObject[] arrayIXObject) {
            this.mArrayClassImplement = arrayClassImplement;
            if (null != arrayIXObject) {
                this.mListIXObject = new ArrayList<WeakReference<IXObject>>();
                for (int nIndex = 0; nIndex < arrayIXObject.length; nIndex++) {
                    WeakReference<IXObject> wr = new WeakReference<IXObject>(arrayIXObject[nIndex]);
                    this.mListIXObject.add(wr);
                }
            }
        }
    }
}
