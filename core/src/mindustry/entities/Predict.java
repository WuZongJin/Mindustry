package mindustry.entities;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

/**
 * Class for predicting shoot angles based on velocities of targets.
 */
public class Predict{
    private static Vec2 vec = new Vec2();
    private static Vec2 vresult = new Vec2();

    /**
     * Calculates of intercept of a stationary and moving target. Do not call from multiple threads!
     * @param srcx X of shooter
     * @param srcy Y of shooter
     * @param dstx X of target
     * @param dsty Y of target
     * @param dstvx X velocity of target (subtract shooter X velocity if needed)
     * @param dstvy Y velocity of target (subtract shooter Y velocity if needed)
     * @param v speed of bullet
     * @return the intercept location
     */
    public static Vec2 intercept(float srcx, float srcy, float dstx, float dsty, float dstvx, float dstvy, float v){
        dstvx /= Time.delta();
        dstvy /= Time.delta();
        float tx = dstx - srcx,
        ty = dsty - srcy;

        // Get quadratic equation components
        float a = dstvx * dstvx + dstvy * dstvy - v * v;
        float b = 2 * (dstvx * tx + dstvy * ty);
        float c = tx * tx + ty * ty;

        // Solve quadratic
        Vec2 ts = quad(a, b, c);

        // Find smallest positive solution
        Vec2 sol = vresult.set(dstx, dsty);
        if(ts != null){
            float t0 = ts.x, t1 = ts.y;
            float t = Math.min(t0, t1);
            if(t < 0) t = Math.max(t0, t1);
            if(t > 0){
                sol.set(dstx + dstvx * t, dsty + dstvy * t);
            }
        }

        return sol;
    }

    /**
     * See {@link #intercept(float, float, float, float, float, float, float)}.
     */
    //public static Vec2 intercept(TargetTrait src, TargetTrait dst, float v){
    //    return intercept(src.getX(), src.getY(), dst.getX(), dst.getY(), dst.getTargetVelocityX() - src.getTargetVelocityX()/(2f*Time.delta()), dst.getTargetVelocityY() - src.getTargetVelocityY()/(2f*Time.delta()), v);
    //}

    private static Vec2 quad(float a, float b, float c){
        Vec2 sol = null;
        if(Math.abs(a) < 1e-6){
            if(Math.abs(b) < 1e-6){
                sol = Math.abs(c) < 1e-6 ? vec.set(0, 0) : null;
            }else{
                vec.set(-c / b, -c / b);
            }
        }else{
            float disc = b * b - 4 * a * c;
            if(disc >= 0){
                disc = Mathf.sqrt(disc);
                a = 2 * a;
                sol = vec.set((-b - disc) / a, (-b + disc) / a);
            }
        }
        return sol;
    }
}
