package com.yc.liaolive.permissions;

/**
 * 权限
 * Created by yangxueqin on 18/12/14.
 */

public class Permission {
    public final String name;

    public final boolean granted;

    public boolean shouldShowRequestPermissionRationale = true;

    public Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationale) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Permission that = (Permission) o;

        if (granted != that.granted) {
            return false;
        }
        if (shouldShowRequestPermissionRationale != that.shouldShowRequestPermissionRationale) {
            return false;
        }
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (granted ? 1 : 0) + (shouldShowRequestPermissionRationale ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted + "shouldShowRequestPermissionRationale=" +
                shouldShowRequestPermissionRationale +
                '}';
    }
}