package org.modelio.module.javadesigner.custom;


class JavaCustomType {
    private String id;

    private String javaType;

    private String wrappedType;

    private String javaImport;


    JavaCustomType(String id) {
        this.id = id;
        this.javaType = "";
        this.wrappedType = "";
        this.javaImport = "";
    }

    public String getJavaType() {
        return this.javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String computeType(boolean useWrapper) {
        if (useWrapper && this.wrappedType != null &&
                this.wrappedType.length () != 0) {
            return this.wrappedType;
        }
        return this.javaType;
    }

    public String getWrappedType() {
        return this.wrappedType;
    }

    public void setWrappedType(String wrappedType) {
        this.wrappedType = wrappedType;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "id=\"" + this.id + "\" javaType=\"" + this.javaType +
                "\" wrappedType=\"" + this.wrappedType + "\" javaImport=\"" +
                this.javaImport + "\"";
    }

    public String getJavaImport() {
        return this.javaImport;
    }

    public void setJavaImport(String javaImport) {
        this.javaImport = javaImport;
    }

}
