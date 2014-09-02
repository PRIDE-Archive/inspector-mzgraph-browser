package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation;

import org.jfree.data.xy.XYDataItem;

/**
 * Fragment ion annotation implementation.
 *
 * User: rwang
 * Date: 09-Jun-2010
 * Time: 19:27:17
 */
public class IonAnnotation extends XYDataItem implements PeakAnnotation {
    private Number mz;
    private Number intensity;
    private IonAnnotationInfo info;

    public IonAnnotation(Number mz, Number intensity, IonAnnotationInfo info) {
        super(mz, intensity);
        this.mz = mz;
        this.intensity = intensity;
        this.info = info;
    }

    public IonAnnotation(double mz, double intensity, IonAnnotationInfo info) {
        super(mz, intensity);
        this.mz = mz;
        this.intensity = intensity;
        this.info = info;
    }

    @Override
    public Number getMz() {
        return this.getX();
    }

    @Override
    public Number getIntensity() {
        return this.getY();
    }

    @Override
    public IonAnnotationInfo getAnnotationInfo() {
        return info;
    }

    public void setInfo(IonAnnotationInfo info) {
        this.info = info;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        IonAnnotation newAnnotation = (IonAnnotation) super.clone();
        newAnnotation.info = (IonAnnotationInfo) info.clone();

        return newAnnotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        IonAnnotation that = (IonAnnotation) o;

        if (intensity != null ? !intensity.equals(that.intensity) : that.intensity != null) return false;
        if (mz != null ? !mz.equals(that.mz) : that.mz != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mz != null ? mz.hashCode() : 0);
        result = 31 * result + (intensity != null ? intensity.hashCode() : 0);
        return result;
    }
}
