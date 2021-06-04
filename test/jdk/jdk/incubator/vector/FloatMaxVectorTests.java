/*
 * Copyright (c) 2018, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @modules jdk.incubator.vector
 * @run testng/othervm -ea -esa -Xbatch -XX:-TieredCompilation FloatMaxVectorTests
 */

// -- This file was mechanically generated: Do not edit! -- //

import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorShuffle;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.Vector;

import jdk.incubator.vector.FloatVector;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.Integer;
import java.util.List;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Test
public class FloatMaxVectorTests extends AbstractVectorTest {

    static final VectorSpecies<Float> SPECIES =
                FloatVector.SPECIES_MAX;

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 100);

    static VectorShape getMaxBit() {
        return VectorShape.S_Max_BIT;
    }

    private static final int Max = 256;  // juts so we can do N/Max

    static final int BUFFER_REPS = Integer.getInteger("jdk.incubator.vector.test.buffer-vectors", 25000 / Max);

    static final int BUFFER_SIZE = Integer.getInteger("jdk.incubator.vector.test.buffer-size", BUFFER_REPS * (Max / 8));

    interface FUnOp {
        float apply(float a);
    }

    static void assertArraysEquals(float[] r, float[] a, FUnOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i]), "at index #" + i + ", input = " + a[i]);
        }
    }

    interface FUnArrayOp {
        float[] apply(float a);
    }

    static void assertArraysEquals(float[] r, float[] a, FUnArrayOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a[i]));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(a[i]);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    static void assertArraysEquals(float[] r, float[] a, boolean[] mask, FUnOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? f.apply(a[i]) : a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? f.apply(a[i]) : a[i], "at index #" + i + ", input = " + a[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    interface FReductionOp {
        float apply(float[] a, int idx);
    }

    interface FReductionAllOp {
        float apply(float[] a);
    }

    static void assertReductionArraysEquals(float[] r, float rc, float[] a,
                                            FReductionOp f, FReductionAllOp fa) {
        int i = 0;
        try {
            Assert.assertEquals(rc, fa.apply(a));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rc, fa.apply(a), "Final result is incorrect!");
            Assert.assertEquals(r[i], f.apply(a, i), "at index #" + i);
        }
    }

    interface FReductionMaskedOp {
        float apply(float[] a, int idx, boolean[] mask);
    }

    interface FReductionAllMaskedOp {
        float apply(float[] a, boolean[] mask);
    }

    static void assertReductionArraysEqualsMasked(float[] r, float rc, float[] a, boolean[] mask,
                                            FReductionMaskedOp f, FReductionAllMaskedOp fa) {
        int i = 0;
        try {
            Assert.assertEquals(rc, fa.apply(a, mask));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i, mask));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rc, fa.apply(a, mask), "Final result is incorrect!");
            Assert.assertEquals(r[i], f.apply(a, i, mask), "at index #" + i);
        }
    }

    interface FReductionOpLong {
        long apply(float[] a, int idx);
    }

    interface FReductionAllOpLong {
        long apply(float[] a);
    }

    static void assertReductionLongArraysEquals(long[] r, long rc, float[] a,
                                            FReductionOpLong f, FReductionAllOpLong fa) {
        int i = 0;
        try {
            Assert.assertEquals(rc, fa.apply(a));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rc, fa.apply(a), "Final result is incorrect!");
            Assert.assertEquals(r[i], f.apply(a, i), "at index #" + i);
        }
    }

    interface FReductionMaskedOpLong {
        long apply(float[] a, int idx, boolean[] mask);
    }

    interface FReductionAllMaskedOpLong {
        long apply(float[] a, boolean[] mask);
    }

    static void assertReductionLongArraysEqualsMasked(long[] r, long rc, float[] a, boolean[] mask,
                                            FReductionMaskedOpLong f, FReductionAllMaskedOpLong fa) {
        int i = 0;
        try {
            Assert.assertEquals(rc, fa.apply(a, mask));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i, mask));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(rc, fa.apply(a, mask), "Final result is incorrect!");
            Assert.assertEquals(r[i], f.apply(a, i, mask), "at index #" + i);
        }
    }

    interface FBoolReductionOp {
        boolean apply(boolean[] a, int idx);
    }

    static void assertReductionBoolArraysEquals(boolean[] r, boolean[] a, FBoolReductionOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a, i), "at index #" + i);
        }
    }

    static void assertInsertArraysEquals(float[] r, float[] a, float element, int index) {
        int i = 0;
        try {
            for (; i < a.length; i += 1) {
                if(i%SPECIES.length() == index) {
                    Assert.assertEquals(r[i], element);
                } else {
                    Assert.assertEquals(r[i], a[i]);
                }
            }
        } catch (AssertionError e) {
            if (i%SPECIES.length() == index) {
                Assert.assertEquals(r[i], element, "at index #" + i);
            } else {
                Assert.assertEquals(r[i], a[i], "at index #" + i);
            }
        }
    }

    static void assertRearrangeArraysEquals(float[] r, float[] a, int[] order, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    Assert.assertEquals(r[i+j], a[i+order[i+j]]);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            Assert.assertEquals(r[i+j], a[i+order[i+j]], "at index #" + idx + ", input = " + a[i+order[i+j]]);
        }
    }

    static void assertSelectFromArraysEquals(float[] r, float[] a, float[] order, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    Assert.assertEquals(r[i+j], a[i+(int)order[i+j]]);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            Assert.assertEquals(r[i+j], a[i+(int)order[i+j]], "at index #" + idx + ", input = " + a[i+(int)order[i+j]]);
        }
    }

    static void assertRearrangeArraysEquals(float[] r, float[] a, int[] order, boolean[] mask, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    if (mask[j % SPECIES.length()])
                         Assert.assertEquals(r[i+j], a[i+order[i+j]]);
                    else
                         Assert.assertEquals(r[i+j], (float)0);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            if (mask[j % SPECIES.length()])
                Assert.assertEquals(r[i+j], a[i+order[i+j]], "at index #" + idx + ", input = " + a[i+order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
            else
                Assert.assertEquals(r[i+j], (float)0, "at index #" + idx + ", input = " + a[i+order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
        }
    }

    static void assertSelectFromArraysEquals(float[] r, float[] a, float[] order, boolean[] mask, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    if (mask[j % SPECIES.length()])
                         Assert.assertEquals(r[i+j], a[i+(int)order[i+j]]);
                    else
                         Assert.assertEquals(r[i+j], (float)0);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            if (mask[j % SPECIES.length()])
                Assert.assertEquals(r[i+j], a[i+(int)order[i+j]], "at index #" + idx + ", input = " + a[i+(int)order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
            else
                Assert.assertEquals(r[i+j], (float)0, "at index #" + idx + ", input = " + a[i+(int)order[i+j]] + ", mask = " + mask[j % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(float[] r, float[] a) {
        int i = 0;
        for (; i < a.length; i += SPECIES.length()) {
            int idx = i;
            for (int j = idx; j < (idx + SPECIES.length()); j++)
                a[j]=a[idx];
        }

        try {
            for (i = 0; i < a.length; i++) {
                Assert.assertEquals(r[i], a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], a[i], "at index #" + i + ", input = " + a[i]);
        }
    }

    interface FBinOp {
        float apply(float a, float b);
    }

    interface FBinMaskOp {
        float apply(float a, float b, boolean m);

        static FBinMaskOp lift(FBinOp f) {
            return (a, b, m) -> m ? f.apply(a, b) : a;
        }
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i]), "(" + a[i] + ", " + b[i] + ") at index #" + i);
        }
    }

    static void assertBroadcastArraysEquals(float[] r, float[] a, float[] b, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]),
                                "(" + a[i] + ", " + b[(i / SPECIES.length()) * SPECIES.length()] + ") at index #" + i);
        }
    }

    static void assertBroadcastLongArraysEquals(float[] r, float[] a, float[] b, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], (float)((long)b[(i / SPECIES.length()) * SPECIES.length()])));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], (float)((long)b[(i / SPECIES.length()) * SPECIES.length()])),
                                "(" + a[i] + ", " + b[(i / SPECIES.length()) * SPECIES.length()] + ") at index #" + i);
        }
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, boolean[] mask, FBinOp f) {
        assertArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(float[] r, float[] a, float[] b, boolean[] mask, FBinOp f) {
        assertBroadcastArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(float[] r, float[] a, float[] b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastLongArraysEquals(float[] r, float[] a, float[] b, boolean[] mask, FBinOp f) {
        assertBroadcastLongArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertBroadcastLongArraysEquals(float[] r, float[] a, float[] b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], (float)((long)b[(i / SPECIES.length()) * SPECIES.length()]), mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], (float)((long)b[(i / SPECIES.length()) * SPECIES.length()]),
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertShiftArraysEquals(float[] r, float[] a, float[] b, FBinOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j]));
                }
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j]), "at index #" + i + ", " + j);
        }
    }

    static void assertShiftArraysEquals(float[] r, float[] a, float[] b, boolean[] mask, FBinOp f) {
        assertShiftArraysEquals(r, a, b, mask, FBinMaskOp.lift(f));
    }

    static void assertShiftArraysEquals(float[] r, float[] a, float[] b, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j], mask[i]));
                }
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j], mask[i]), "at index #" + i + ", input1 = " + a[i+j] + ", input2 = " + b[j] + ", mask = " + mask[i]);
        }
    }

    interface FTernOp {
        float apply(float a, float b, float c);
    }

    interface FTernMaskOp {
        float apply(float a, float b, float c, boolean m);

        static FTernMaskOp lift(FTernOp f) {
            return (a, b, c, m) -> m ? f.apply(a, b, c) : a;
        }
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, float[] c, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i]);
        }
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, float[] c, boolean[] mask, FTernOp f) {
        assertArraysEquals(r, a, b, c, mask, FTernMaskOp.lift(f));
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, float[] c, boolean[] mask, FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = "
              + b[i] + ", input3 = " + c[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()]), "at index #" +
                                i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " +
                                c[(i / SPECIES.length()) * SPECIES.length()]);
        }
    }

    static void assertAltBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i]), "at index #" +
                                i + ", input1 = " + a[i] + ", input2 = " +
                                b[(i / SPECIES.length()) * SPECIES.length()] + ",  input3 = " + c[i]);
        }
    }

    static void assertBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, boolean[] mask,
                                            FTernOp f) {
        assertBroadcastArraysEquals(r, a, b, c, mask, FTernMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, boolean[] mask,
                                            FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()],
                                    mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " +
                                b[i] + ", input3 = " + c[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertAltBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, boolean[] mask,
                                            FTernOp f) {
        assertAltBroadcastArraysEquals(r, a, b, c, mask, FTernMaskOp.lift(f));
    }

    static void assertAltBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, boolean[] mask,
                                            FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i],
                                    mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] +
                                ", input3 = " + c[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertDoubleBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                    c[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                c[(i / SPECIES.length()) * SPECIES.length()]), "at index #" + i + ", input1 = " + a[i]
                                + ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", input3 = " +
                                c[(i / SPECIES.length()) * SPECIES.length()]);
        }
    }

    static void assertDoubleBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, boolean[] mask,
                                                  FTernOp f) {
        assertDoubleBroadcastArraysEquals(r, a, b, c, mask, FTernMaskOp.lift(f));
    }

    static void assertDoubleBroadcastArraysEquals(float[] r, float[] a, float[] b, float[] c, boolean[] mask,
                                                  FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                    c[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                c[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]), "at index #"
                                + i + ", input1 = " + a[i] + ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] +
                                ", input3 = " + c[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }


    static boolean isWithin1Ulp(float actual, float expected) {
        if (Float.isNaN(expected) && !Float.isNaN(actual)) {
            return false;
        } else if (!Float.isNaN(expected) && Float.isNaN(actual)) {
            return false;
        }

        float low = Math.nextDown(expected);
        float high = Math.nextUp(expected);

        if (Float.compare(low, expected) > 0) {
            return false;
        }

        if (Float.compare(high, expected) < 0) {
            return false;
        }

        return true;
    }

    static void assertArraysEqualsWithinOneUlp(float[] r, float[] a, FUnOp mathf, FUnOp strictmathf) {
        int i = 0;
        try {
            // Check that result is within 1 ulp of strict math or equivalent to math implementation.
            for (; i < a.length; i++) {
                Assert.assertTrue(Float.compare(r[i], mathf.apply(a[i])) == 0 ||
                                    isWithin1Ulp(r[i], strictmathf.apply(a[i])));
            }
        } catch (AssertionError e) {
            Assert.assertTrue(Float.compare(r[i], mathf.apply(a[i])) == 0, "at index #" + i + ", input = " + a[i] + ", actual = " + r[i] + ", expected = " + mathf.apply(a[i]));
            Assert.assertTrue(isWithin1Ulp(r[i], strictmathf.apply(a[i])), "at index #" + i + ", input = " + a[i] + ", actual = " + r[i] + ", expected (within 1 ulp) = " + strictmathf.apply(a[i]));
        }
    }

    static void assertArraysEqualsWithinOneUlp(float[] r, float[] a, float[] b, FBinOp mathf, FBinOp strictmathf) {
        int i = 0;
        try {
            // Check that result is within 1 ulp of strict math or equivalent to math implementation.
            for (; i < a.length; i++) {
                Assert.assertTrue(Float.compare(r[i], mathf.apply(a[i], b[i])) == 0 ||
                                    isWithin1Ulp(r[i], strictmathf.apply(a[i], b[i])));
            }
        } catch (AssertionError e) {
            Assert.assertTrue(Float.compare(r[i], mathf.apply(a[i], b[i])) == 0, "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", actual = " + r[i] + ", expected = " + mathf.apply(a[i], b[i]));
            Assert.assertTrue(isWithin1Ulp(r[i], strictmathf.apply(a[i], b[i])), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", actual = " + r[i] + ", expected (within 1 ulp) = " + strictmathf.apply(a[i], b[i]));
        }
    }

    static void assertBroadcastArraysEqualsWithinOneUlp(float[] r, float[] a, float[] b,
                                                        FBinOp mathf, FBinOp strictmathf) {
        int i = 0;
        try {
            // Check that result is within 1 ulp of strict math or equivalent to math implementation.
            for (; i < a.length; i++) {
                Assert.assertTrue(Float.compare(r[i],
                                  mathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()])) == 0 ||
                                  isWithin1Ulp(r[i],
                                  strictmathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()])));
            }
        } catch (AssertionError e) {
            Assert.assertTrue(Float.compare(r[i],
                              mathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()])) == 0,
                              "at index #" + i + ", input1 = " + a[i] + ", input2 = " +
                              b[(i / SPECIES.length()) * SPECIES.length()] + ", actual = " + r[i] +
                              ", expected = " + mathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]));
            Assert.assertTrue(isWithin1Ulp(r[i],
                              strictmathf.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()])),
                             "at index #" + i + ", input1 = " + a[i] + ", input2 = " +
                             b[(i / SPECIES.length()) * SPECIES.length()] + ", actual = " + r[i] +
                             ", expected (within 1 ulp) = " + strictmathf.apply(a[i],
                             b[(i / SPECIES.length()) * SPECIES.length()]));
        }
    }

    interface FBinArrayOp {
        float apply(float[] a, int b);
    }

    static void assertArraysEquals(float[] r, float[] a, FBinArrayOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a,i), "at index #" + i);
        }
    }

    interface FGatherScatterOp {
        float[] apply(float[] a, int ix, int[] b, int iy);
    }

    static void assertArraysEquals(float[] r, float[] a, int[] b, FGatherScatterOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, b, i));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(a, i, b, i);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + " at index #" + i);
        }
    }

    interface FGatherMaskedOp {
        float[] apply(float[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    interface FScatterMaskedOp {
        float[] apply(float[] r, float[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    static void assertArraysEquals(float[] r, float[] a, int[] b, boolean[] mask, FGatherMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(a, i, mask, b, i);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + ", mask: "
              + Arrays.toString(mask)
              + " at index #" + i);
        }
    }

    static void assertArraysEquals(float[] r, float[] a, int[] b, boolean[] mask, FScatterMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(r, a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(r, a, i, mask, b, i);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + ", r: "
              + Arrays.toString(Arrays.copyOfRange(r, i, i+SPECIES.length()))
              + ", mask: "
              + Arrays.toString(mask)
              + " at index #" + i);
        }
    }

    interface FLaneOp {
        float[] apply(float[] a, int origin, int idx);
    }

    static void assertArraysEquals(float[] r, float[] a, int origin, FLaneOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, origin, i));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(a, origin, i);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    interface FLaneBop {
        float[] apply(float[] a, float[] b, int origin, int idx);
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, int origin, FLaneBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, i));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(a, b, origin, i);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLaneMaskedBop {
        float[] apply(float[] a, float[] b, int origin, boolean[] mask, int idx);
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, int origin, boolean[] mask, FLaneMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, mask, i));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(a, b, origin, mask, i);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLanePartBop {
        float[] apply(float[] a, float[] b, int origin, int part, int idx);
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, int origin, int part, FLanePartBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, i));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(a, b, origin, part, i);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    interface FLanePartMaskedBop {
        float[] apply(float[] a, float[] b, int origin, int part, boolean[] mask, int idx);
    }

    static void assertArraysEquals(float[] r, float[] a, float[] b, int origin, int part, boolean[] mask, FLanePartMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, mask, i));
            }
        } catch (AssertionError e) {
            float[] ref = f.apply(a, b, origin, part, mask, i);
            float[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    static int intCornerCaseValue(int i) {
        switch(i % 5) {
            case 0:
                return Integer.MAX_VALUE;
            case 1:
                return Integer.MIN_VALUE;
            case 2:
                return Integer.MIN_VALUE;
            case 3:
                return Integer.MAX_VALUE;
            default:
                return (int)0;
        }
    }

    static final List<IntFunction<float[]>> INT_FLOAT_GENERATORS = List.of(
            withToString("float[-i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(-i * 5));
            }),
            withToString("float[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(i * 5));
            }),
            withToString("float[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((float)(i + 1) == 0) ? 1 : (float)(i + 1)));
            }),
            withToString("float[intCornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)intCornerCaseValue(i));
            })
    );

    static void assertArraysEquals(int[] r, float[] a, int offs) {
        int i = 0;
        try {
            for (; i < r.length; i++) {
                Assert.assertEquals(r[i], (int)(a[i+offs]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], (int)(a[i+offs]), "at index #" + i + ", input = " + a[i+offs]);
        }
    }

    static long longCornerCaseValue(int i) {
        switch(i % 5) {
            case 0:
                return Long.MAX_VALUE;
            case 1:
                return Long.MIN_VALUE;
            case 2:
                return Long.MIN_VALUE;
            case 3:
                return Long.MAX_VALUE;
            default:
                return (long)0;
        }
    }

    static final List<IntFunction<float[]>> LONG_FLOAT_GENERATORS = List.of(
            withToString("float[-i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(-i * 5));
            }),
            withToString("float[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(i * 5));
            }),
            withToString("float[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((float)(i + 1) == 0) ? 1 : (float)(i + 1)));
            }),
            withToString("float[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)longCornerCaseValue(i));
            })
    );


    static void assertArraysEquals(long[] r, float[] a, int offs) {
        int i = 0;
        try {
            for (; i < r.length; i++) {
                Assert.assertEquals(r[i], (long)(a[i+offs]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], (long)(a[i+offs]), "at index #" + i + ", input = " + a[i+offs]);
        }
    }

    static void assertArraysEquals(double[] r, float[] a, int offs) {
        int i = 0;
        try {
            for (; i < r.length; i++) {
                Assert.assertEquals(r[i], (double)(a[i+offs]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], (double)(a[i+offs]), "at index #" + i + ", input = " + a[i+offs]);
        }
    }


    static int bits(float e) {
        return  Float.floatToIntBits(e);
    }

    static final List<IntFunction<float[]>> FLOAT_GENERATORS = List.of(
            withToString("float[-i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(-i * 5));
            }),
            withToString("float[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(i * 5));
            }),
            withToString("float[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((float)(i + 1) == 0) ? 1 : (float)(i + 1)));
            }),
            withToString("float[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    // Create combinations of pairs
    // @@@ Might be sensitive to order e.g. div by 0
    static final List<List<IntFunction<float[]>>> FLOAT_GENERATOR_PAIRS =
        Stream.of(FLOAT_GENERATORS.get(0)).
                flatMap(fa -> FLOAT_GENERATORS.stream().skip(1).map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] boolUnaryOpProvider() {
        return BOOL_ARRAY_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<float[]>>> FLOAT_GENERATOR_TRIPLES =
        FLOAT_GENERATOR_PAIRS.stream().
                flatMap(pair -> FLOAT_GENERATORS.stream().map(f -> List.of(pair.get(0), pair.get(1), f))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] floatBinaryOpProvider() {
        return FLOAT_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatIndexedOpProvider() {
        return FLOAT_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatBinaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> FLOAT_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatTernaryOpProvider() {
        return FLOAT_GENERATOR_TRIPLES.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatTernaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> FLOAT_GENERATOR_TRIPLES.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatUnaryOpProvider() {
        return FLOAT_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatUnaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> FLOAT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floattoIntUnaryOpProvider() {
        return INT_FLOAT_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floattoLongUnaryOpProvider() {
        return LONG_FLOAT_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] maskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] maskCompareOpProvider() {
        return BOOLEAN_MASK_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shuffleProvider() {
        return INT_SHUFFLE_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shuffleCompareOpProvider() {
        return INT_SHUFFLE_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatUnaryOpShuffleProvider() {
        return INT_SHUFFLE_GENERATORS.stream().
                flatMap(fs -> FLOAT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatUnaryOpShuffleMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> INT_SHUFFLE_GENERATORS.stream().
                    flatMap(fs -> FLOAT_GENERATORS.stream().map(fa -> {
                        return new Object[] {fa, fs, fm};
                }))).
                toArray(Object[][]::new);
    }

    static final List<BiFunction<Integer,Integer,float[]>> FLOAT_SHUFFLE_GENERATORS = List.of(
            withToStringBi("shuffle[random]", (Integer l, Integer m) -> {
                float[] a = new float[l];
                int upper = m;
                for (int i = 0; i < 1; i++) {
                    a[i] = (float)RAND.nextInt(upper);
                }
                return a;
            })
    );

    @DataProvider
    public Object[][] floatUnaryOpSelectFromProvider() {
        return FLOAT_SHUFFLE_GENERATORS.stream().
                flatMap(fs -> FLOAT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatUnaryOpSelectFromMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> FLOAT_SHUFFLE_GENERATORS.stream().
                    flatMap(fs -> FLOAT_GENERATORS.stream().map(fa -> {
                        return new Object[] {fa, fs, fm};
                }))).
                toArray(Object[][]::new);
    }


    @DataProvider
    public Object[][] floatUnaryOpIndexProvider() {
        return INT_INDEX_GENERATORS.stream().
                flatMap(fs -> FLOAT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatUnaryMaskedOpIndexProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            FLOAT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm, fs};
            }))).
            toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] scatterMaskedOpIndexProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            FLOAT_GENERATORS.stream().flatMap(fn ->
              FLOAT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fn, fm, fs};
            })))).
            toArray(Object[][]::new);
    }

    static final List<IntFunction<float[]>> FLOAT_COMPARE_GENERATORS = List.of(
            withToString("float[i]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)i);
            }),
            withToString("float[i - length / 2]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(i - (s * BUFFER_REPS / 2)));
            }),
            withToString("float[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(i + 1));
            }),
            withToString("float[i - 2]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (float)(i - 2));
            }),
            withToString("float[zigZag(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> i%3 == 0 ? (float)i : (i%3 == 1 ? (float)(i + 1) : (float)(i - 2)));
            }),
            withToString("float[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    static final List<List<IntFunction<float[]>>> FLOAT_TEST_GENERATOR_ARGS =
        FLOAT_COMPARE_GENERATORS.stream().
                map(fa -> List.of(fa)).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] floatTestOpProvider() {
        return FLOAT_TEST_GENERATOR_ARGS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatTestOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> FLOAT_TEST_GENERATOR_ARGS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<float[]>>> FLOAT_COMPARE_GENERATOR_PAIRS =
        FLOAT_COMPARE_GENERATORS.stream().
                flatMap(fa -> FLOAT_COMPARE_GENERATORS.stream().map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] floatCompareOpProvider() {
        return FLOAT_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] floatCompareOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> FLOAT_COMPARE_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    interface ToFloatF {
        float apply(int i);
    }

    static float[] fill(int s , ToFloatF f) {
        return fill(new float[s], f);
    }

    static float[] fill(float[] a, ToFloatF f) {
        for (int i = 0; i < a.length; i++) {
            a[i] = f.apply(i);
        }
        return a;
    }

    static float cornerCaseValue(int i) {
        switch(i % 7) {
            case 0:
                return Float.MAX_VALUE;
            case 1:
                return Float.MIN_VALUE;
            case 2:
                return Float.NEGATIVE_INFINITY;
            case 3:
                return Float.POSITIVE_INFINITY;
            case 4:
                return Float.NaN;
            case 5:
                return (float)0.0;
            default:
                return (float)-0.0;
        }
    }

    static float get(float[] a, int i) {
        return (float) a[i];
    }

    static final IntFunction<float[]> fr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new float[length];
    };

    static final IntFunction<boolean[]> fmr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new boolean[length];
    };

    static final IntFunction<long[]> lfr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new long[length];
    };


    static boolean eq(float a, float b) {
        return a == b;
    }

    static boolean neq(float a, float b) {
        return a != b;
    }

    static boolean lt(float a, float b) {
        return a < b;
    }

    static boolean le(float a, float b) {
        return a <= b;
    }

    static boolean gt(float a, float b) {
        return a > b;
    }

    static boolean ge(float a, float b) {
        return a >= b;
    }


    @Test
    static void smokeTest1() {
        FloatVector three = FloatVector.broadcast(SPECIES, (byte)-3);
        FloatVector three2 = (FloatVector) SPECIES.broadcast(-3);
        assert(three.eq(three2).allTrue());
        FloatVector three3 = three2.broadcast(1).broadcast(-3);
        assert(three.eq(three3).allTrue());
        int scale = 2;
        Class<?> ETYPE = float.class;
        if (ETYPE == double.class || ETYPE == long.class)
            scale = 1000000;
        else if (ETYPE == byte.class && SPECIES.length() >= 64)
            scale = 1;
        FloatVector higher = three.addIndex(scale);
        VectorMask<Float> m = three.compare(VectorOperators.LE, higher);
        assert(m.allTrue());
        m = higher.min((float)-1).test(VectorOperators.IS_NEGATIVE);
        assert(m.allTrue());
        m = higher.test(VectorOperators.IS_FINITE);
        assert(m.allTrue());
        float max = higher.reduceLanes(VectorOperators.MAX);
        assert(max == -3 + scale * (SPECIES.length()-1));
    }

    private static float[]
    bothToArray(FloatVector a, FloatVector b) {
        float[] r = new float[a.length() + b.length()];
        a.intoArray(r, 0);
        b.intoArray(r, a.length());
        return r;
    }

    @Test
    static void smokeTest2() {
        // Do some zipping and shuffling.
        FloatVector io = (FloatVector) SPECIES.broadcast(0).addIndex(1);
        FloatVector io2 = (FloatVector) VectorShuffle.iota(SPECIES,0,1,false).toVector();
        Assert.assertEquals(io, io2);
        FloatVector a = io.add((float)1); //[1,2]
        FloatVector b = a.neg();  //[-1,-2]
        float[] abValues = bothToArray(a,b); //[1,2,-1,-2]
        VectorShuffle<Float> zip0 = VectorShuffle.makeZip(SPECIES, 0);
        VectorShuffle<Float> zip1 = VectorShuffle.makeZip(SPECIES, 1);
        FloatVector zab0 = a.rearrange(zip0,b); //[1,-1]
        FloatVector zab1 = a.rearrange(zip1,b); //[2,-2]
        float[] zabValues = bothToArray(zab0, zab1); //[1,-1,2,-2]
        // manually zip
        float[] manual = new float[zabValues.length];
        for (int i = 0; i < manual.length; i += 2) {
            manual[i+0] = abValues[i/2];
            manual[i+1] = abValues[a.length() + i/2];
        }
        Assert.assertEquals(Arrays.toString(zabValues), Arrays.toString(manual));
        VectorShuffle<Float> unz0 = VectorShuffle.makeUnzip(SPECIES, 0);
        VectorShuffle<Float> unz1 = VectorShuffle.makeUnzip(SPECIES, 1);
        FloatVector uab0 = zab0.rearrange(unz0,zab1);
        FloatVector uab1 = zab0.rearrange(unz1,zab1);
        float[] abValues1 = bothToArray(uab0, uab1);
        Assert.assertEquals(Arrays.toString(abValues), Arrays.toString(abValues1));
    }

    static void iotaShuffle() {
        FloatVector io = (FloatVector) SPECIES.broadcast(0).addIndex(1);
        FloatVector io2 = (FloatVector) VectorShuffle.iota(SPECIES, 0 , 1, false).toVector();
        Assert.assertEquals(io, io2);
    }

    @Test
    // Test all shuffle related operations.
    static void shuffleTest() {
        // To test backend instructions, make sure that C2 is used.
        for (int loop = 0; loop < INVOC_COUNT * INVOC_COUNT; loop++) {
            iotaShuffle();
        }
    }

    @Test
    void viewAsIntegeralLanesTest() {
        Vector<?> asIntegral = SPECIES.zero().viewAsIntegralLanes();
        VectorSpecies<?> asIntegralSpecies = asIntegral.species();
        Assert.assertNotEquals(asIntegralSpecies.elementType(), SPECIES.elementType());
        Assert.assertEquals(asIntegralSpecies.vectorShape(), SPECIES.vectorShape());
        Assert.assertEquals(asIntegralSpecies.length(), SPECIES.length());
        Assert.assertEquals(asIntegral.viewAsFloatingLanes().species(), SPECIES);
    }

    @Test
    void viewAsFloatingLanesTest() {
        Vector<?> asFloating = SPECIES.zero().viewAsFloatingLanes();
        Assert.assertEquals(asFloating.species(), SPECIES);
    }

    static float ADD(float a, float b) {
        return (float)(a + b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void ADDFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::ADD);
    }
    static float add(float a, float b) {
        return (float)(a + b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void addFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.add(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::add);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void ADDFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::ADD);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void addFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.add(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::add);
    }
    static float SUB(float a, float b) {
        return (float)(a - b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void SUBFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::SUB);
    }
    static float sub(float a, float b) {
        return (float)(a - b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void subFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.sub(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::sub);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void SUBFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::SUB);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void subFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.sub(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::sub);
    }
    static float MUL(float a, float b) {
        return (float)(a * b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void MULFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::MUL);
    }
    static float mul(float a, float b) {
        return (float)(a * b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void mulFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.mul(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::mul);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void MULFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::MUL);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void mulFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.mul(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::mul);
    }

    static float DIV(float a, float b) {
        return (float)(a / b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void DIVFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::DIV);
    }
    static float div(float a, float b) {
        return (float)(a / b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void divFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.div(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::div);
    }



    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void DIVFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::DIV);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void divFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.div(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::div);
    }



    static float FIRST_NONZERO(float a, float b) {
        return (float)(Double.doubleToLongBits(a)!=0?a:b);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void FIRST_NONZEROFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::FIRST_NONZERO);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void FIRST_NONZEROFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::FIRST_NONZERO);
    }









    @Test(dataProvider = "floatBinaryOpProvider")
    static void addFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.add(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, FloatMaxVectorTests::add);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void addFloatMaxVectorTestsBroadcastMaskedSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.add(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, FloatMaxVectorTests::add);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void subFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.sub(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, FloatMaxVectorTests::sub);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void subFloatMaxVectorTestsBroadcastMaskedSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.sub(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, FloatMaxVectorTests::sub);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void mulFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.mul(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, FloatMaxVectorTests::mul);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void mulFloatMaxVectorTestsBroadcastMaskedSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.mul(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, FloatMaxVectorTests::mul);
    }


    @Test(dataProvider = "floatBinaryOpProvider")
    static void divFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.div(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, FloatMaxVectorTests::div);
    }



    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void divFloatMaxVectorTestsBroadcastMaskedSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.div(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, mask, FloatMaxVectorTests::div);
    }










    @Test(dataProvider = "floatBinaryOpProvider")
    static void ADDFloatMaxVectorTestsBroadcastLongSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.ADD, (long)b[i]).intoArray(r, i);
        }

        assertBroadcastLongArraysEquals(r, a, b, FloatMaxVectorTests::ADD);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void ADDFloatMaxVectorTestsBroadcastMaskedLongSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.ADD, (long)b[i], vmask).intoArray(r, i);
        }

        assertBroadcastLongArraysEquals(r, a, b, mask, FloatMaxVectorTests::ADD);
    }




































    static float MIN(float a, float b) {
        return (float)(Math.min(a, b));
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void MINFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MIN, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::MIN);
    }
    static float min(float a, float b) {
        return (float)(Math.min(a, b));
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void minFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.min(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::min);
    }
    static float MAX(float a, float b) {
        return (float)(Math.max(a, b));
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void MAXFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MAX, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::MAX);
    }
    static float max(float a, float b) {
        return (float)(Math.max(a, b));
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void maxFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.max(bv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::max);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void MINFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MIN, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, FloatMaxVectorTests::MIN);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void minFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.min(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, FloatMaxVectorTests::min);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void MAXFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MAX, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, FloatMaxVectorTests::MAX);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void maxFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.max(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, FloatMaxVectorTests::max);
    }












    static float ADDReduce(float[] a, int idx) {
        float res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res += a[i];
        }

        return res;
    }

    static float ADDReduceAll(float[] a) {
        float res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDReduce(a, i);
        }

        return res;
    }
    @Test(dataProvider = "floatUnaryOpProvider")
    static void ADDReduceFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        float ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD);
            }
        }

        assertReductionArraysEquals(r, ra, a,
                FloatMaxVectorTests::ADDReduce, FloatMaxVectorTests::ADDReduceAll);
    }
    static float ADDReduceMasked(float[] a, int idx, boolean[] mask) {
        float res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res += a[i];
        }

        return res;
    }

    static float ADDReduceAllMasked(float[] a, boolean[] mask) {
        float res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDReduceMasked(a, i, mask);
        }

        return res;
    }
    @Test(dataProvider = "floatUnaryOpMaskProvider")
    static void ADDReduceFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        float ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                FloatMaxVectorTests::ADDReduceMasked, FloatMaxVectorTests::ADDReduceAllMasked);
    }
    static float MULReduce(float[] a, int idx) {
        float res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res *= a[i];
        }

        return res;
    }

    static float MULReduceAll(float[] a) {
        float res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res *= MULReduce(a, i);
        }

        return res;
    }
    @Test(dataProvider = "floatUnaryOpProvider")
    static void MULReduceFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        float ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL);
            }
        }

        assertReductionArraysEquals(r, ra, a,
                FloatMaxVectorTests::MULReduce, FloatMaxVectorTests::MULReduceAll);
    }
    static float MULReduceMasked(float[] a, int idx, boolean[] mask) {
        float res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if (mask[i % SPECIES.length()])
                res *= a[i];
        }

        return res;
    }

    static float MULReduceAllMasked(float[] a, boolean[] mask) {
        float res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res *= MULReduceMasked(a, i, mask);
        }

        return res;
    }
    @Test(dataProvider = "floatUnaryOpMaskProvider")
    static void MULReduceFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        float ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                FloatMaxVectorTests::MULReduceMasked, FloatMaxVectorTests::MULReduceAllMasked);
    }
    static float MINReduce(float[] a, int idx) {
        float res = Float.POSITIVE_INFINITY;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (float)Math.min(res, a[i]);
        }

        return res;
    }

    static float MINReduceAll(float[] a) {
        float res = Float.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            res = (float)Math.min(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "floatUnaryOpProvider")
    static void MINReduceFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        float ra = Float.POSITIVE_INFINITY;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Float.POSITIVE_INFINITY;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                ra = (float)Math.min(ra, av.reduceLanes(VectorOperators.MIN));
            }
        }

        assertReductionArraysEquals(r, ra, a,
                FloatMaxVectorTests::MINReduce, FloatMaxVectorTests::MINReduceAll);
    }
    static float MINReduceMasked(float[] a, int idx, boolean[] mask) {
        float res = Float.POSITIVE_INFINITY;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res = (float)Math.min(res, a[i]);
        }

        return res;
    }

    static float MINReduceAllMasked(float[] a, boolean[] mask) {
        float res = Float.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if(mask[i % SPECIES.length()])
                res = (float)Math.min(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "floatUnaryOpMaskProvider")
    static void MINReduceFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        float ra = Float.POSITIVE_INFINITY;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Float.POSITIVE_INFINITY;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                ra = (float)Math.min(ra, av.reduceLanes(VectorOperators.MIN, vmask));
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                FloatMaxVectorTests::MINReduceMasked, FloatMaxVectorTests::MINReduceAllMasked);
    }
    static float MAXReduce(float[] a, int idx) {
        float res = Float.NEGATIVE_INFINITY;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (float)Math.max(res, a[i]);
        }

        return res;
    }

    static float MAXReduceAll(float[] a) {
        float res = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            res = (float)Math.max(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "floatUnaryOpProvider")
    static void MAXReduceFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        float ra = Float.NEGATIVE_INFINITY;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                ra = (float)Math.max(ra, av.reduceLanes(VectorOperators.MAX));
            }
        }

        assertReductionArraysEquals(r, ra, a,
                FloatMaxVectorTests::MAXReduce, FloatMaxVectorTests::MAXReduceAll);
    }
    static float MAXReduceMasked(float[] a, int idx, boolean[] mask) {
        float res = Float.NEGATIVE_INFINITY;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res = (float)Math.max(res, a[i]);
        }

        return res;
    }

    static float MAXReduceAllMasked(float[] a, boolean[] mask) {
        float res = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if(mask[i % SPECIES.length()])
                res = (float)Math.max(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "floatUnaryOpMaskProvider")
    static void MAXReduceFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        float ra = Float.NEGATIVE_INFINITY;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                ra = (float)Math.max(ra, av.reduceLanes(VectorOperators.MAX, vmask));
            }
        }

        assertReductionArraysEqualsMasked(r, ra, a, mask,
                FloatMaxVectorTests::MAXReduceMasked, FloatMaxVectorTests::MAXReduceAllMasked);
    }





    @Test(dataProvider = "floatUnaryOpProvider")
    static void withFloatMaxVectorTests(IntFunction<float []> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.withLane(0, (float)4).intoArray(r, i);
            }
        }

        assertInsertArraysEquals(r, a, (float)4, 0);
    }
    static boolean testIS_DEFAULT(float a) {
        return bits(a)==0;
    }

    @Test(dataProvider = "floatTestOpProvider")
    static void IS_DEFAULTFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                VectorMask<Float> mv = av.test(VectorOperators.IS_DEFAULT);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_DEFAULT(a[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatTestOpMaskProvider")
    static void IS_DEFAULTMaskedFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.test(VectorOperators.IS_DEFAULT, vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j),  vmask.laneIsSet(j) && testIS_DEFAULT(a[i + j]));
            }
        }
    }
    static boolean testIS_NEGATIVE(float a) {
        return bits(a)<0;
    }

    @Test(dataProvider = "floatTestOpProvider")
    static void IS_NEGATIVEFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                VectorMask<Float> mv = av.test(VectorOperators.IS_NEGATIVE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_NEGATIVE(a[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatTestOpMaskProvider")
    static void IS_NEGATIVEMaskedFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.test(VectorOperators.IS_NEGATIVE, vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j),  vmask.laneIsSet(j) && testIS_NEGATIVE(a[i + j]));
            }
        }
    }

    static boolean testIS_FINITE(float a) {
        return Float.isFinite(a);
    }

    @Test(dataProvider = "floatTestOpProvider")
    static void IS_FINITEFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                VectorMask<Float> mv = av.test(VectorOperators.IS_FINITE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_FINITE(a[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatTestOpMaskProvider")
    static void IS_FINITEMaskedFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.test(VectorOperators.IS_FINITE, vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j),  vmask.laneIsSet(j) && testIS_FINITE(a[i + j]));
            }
        }
    }


    static boolean testIS_NAN(float a) {
        return Float.isNaN(a);
    }

    @Test(dataProvider = "floatTestOpProvider")
    static void IS_NANFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                VectorMask<Float> mv = av.test(VectorOperators.IS_NAN);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_NAN(a[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatTestOpMaskProvider")
    static void IS_NANMaskedFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.test(VectorOperators.IS_NAN, vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j),  vmask.laneIsSet(j) && testIS_NAN(a[i + j]));
            }
        }
    }


    static boolean testIS_INFINITE(float a) {
        return Float.isInfinite(a);
    }

    @Test(dataProvider = "floatTestOpProvider")
    static void IS_INFINITEFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                VectorMask<Float> mv = av.test(VectorOperators.IS_INFINITE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_INFINITE(a[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatTestOpMaskProvider")
    static void IS_INFINITEMaskedFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.test(VectorOperators.IS_INFINITE, vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j),  vmask.laneIsSet(j) && testIS_INFINITE(a[i + j]));
            }
        }
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void LTFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.LT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), lt(a[i + j], b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void ltFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.lt(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), lt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void LTFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.LT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && lt(a[i + j], b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void GTFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.GT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), gt(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void GTFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.GT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && gt(a[i + j], b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void EQFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.EQ, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), eq(a[i + j], b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void eqFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.eq(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), eq(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void EQFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.EQ, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && eq(a[i + j], b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void NEFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.NE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), neq(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void NEFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.NE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && neq(a[i + j], b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void LEFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.LE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), le(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void LEFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.LE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && le(a[i + j], b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void GEFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.GE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), ge(a[i + j], b[i + j]));
                }
            }
        }
    }

    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void GEFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                VectorMask<Float> mv = av.compare(VectorOperators.GE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && ge(a[i + j], b[i + j]));
                }
            }
        }
    }










    @Test(dataProvider = "floatCompareOpProvider")
    static void LTFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.compare(VectorOperators.LT, b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i]);
            }
        }
    }


    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void LTFloatMaxVectorTestsBroadcastMaskedSmokeTest(IntFunction<float[]> fa,
                                IntFunction<float[]> fb, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.compare(VectorOperators.LT, b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < b[i]));
            }
        }
    }

    @Test(dataProvider = "floatCompareOpProvider")
    static void LTFloatMaxVectorTestsBroadcastLongSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.compare(VectorOperators.LT, (long)b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < (float)((long)b[i]));
            }
        }
    }


    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void LTFloatMaxVectorTestsBroadcastLongMaskedSmokeTest(IntFunction<float[]> fa,
                                IntFunction<float[]> fb, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.compare(VectorOperators.LT, (long)b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < (float)((long)b[i])));
            }
        }
    }

    @Test(dataProvider = "floatCompareOpProvider")
    static void EQFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.compare(VectorOperators.EQ, b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i]);
            }
        }
    }


    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void EQFloatMaxVectorTestsBroadcastMaskedSmokeTest(IntFunction<float[]> fa,
                                IntFunction<float[]> fb, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.compare(VectorOperators.EQ, b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == b[i]));
            }
        }
    }

    @Test(dataProvider = "floatCompareOpProvider")
    static void EQFloatMaxVectorTestsBroadcastLongSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.compare(VectorOperators.EQ, (long)b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == (float)((long)b[i]));
            }
        }
    }


    @Test(dataProvider = "floatCompareOpMaskProvider")
    static void EQFloatMaxVectorTestsBroadcastLongMaskedSmokeTest(IntFunction<float[]> fa,
                                IntFunction<float[]> fb, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.compare(VectorOperators.EQ, (long)b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == (float)((long)b[i])));
            }
        }
    }

    static float blend(float a, float b, boolean mask) {
        return mask ? b : a;
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void blendFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.blend(bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::blend);
    }

    @Test(dataProvider = "floatUnaryOpShuffleProvider")
    static void RearrangeFloatMaxVectorTests(IntFunction<float[]> fa,
                                           BiFunction<Integer,Integer,int[]> fs) {
        float[] a = fa.apply(SPECIES.length());
        int[] order = fs.apply(a.length, SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.rearrange(VectorShuffle.fromArray(SPECIES, order, i)).intoArray(r, i);
            }
        }

        assertRearrangeArraysEquals(r, a, order, SPECIES.length());
    }

    @Test(dataProvider = "floatUnaryOpShuffleMaskProvider")
    static void RearrangeFloatMaxVectorTestsMaskedSmokeTest(IntFunction<float[]> fa,
                                                          BiFunction<Integer,Integer,int[]> fs,
                                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        int[] order = fs.apply(a.length, SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.rearrange(VectorShuffle.fromArray(SPECIES, order, i), vmask).intoArray(r, i);
        }

        assertRearrangeArraysEquals(r, a, order, mask, SPECIES.length());
    }
    @Test(dataProvider = "floatUnaryOpProvider")
    static void getFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                int num_lanes = SPECIES.length();
                // Manually unroll because full unroll happens after intrinsification.
                // Unroll is needed because get intrinsic requires for index to be a known constant.
                if (num_lanes == 1) {
                    r[i]=av.lane(0);
                } else if (num_lanes == 2) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                } else if (num_lanes == 4) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                } else if (num_lanes == 8) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                } else if (num_lanes == 16) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                } else if (num_lanes == 32) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                    r[i+16]=av.lane(16);
                    r[i+17]=av.lane(17);
                    r[i+18]=av.lane(18);
                    r[i+19]=av.lane(19);
                    r[i+20]=av.lane(20);
                    r[i+21]=av.lane(21);
                    r[i+22]=av.lane(22);
                    r[i+23]=av.lane(23);
                    r[i+24]=av.lane(24);
                    r[i+25]=av.lane(25);
                    r[i+26]=av.lane(26);
                    r[i+27]=av.lane(27);
                    r[i+28]=av.lane(28);
                    r[i+29]=av.lane(29);
                    r[i+30]=av.lane(30);
                    r[i+31]=av.lane(31);
                } else if (num_lanes == 64) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                    r[i+16]=av.lane(16);
                    r[i+17]=av.lane(17);
                    r[i+18]=av.lane(18);
                    r[i+19]=av.lane(19);
                    r[i+20]=av.lane(20);
                    r[i+21]=av.lane(21);
                    r[i+22]=av.lane(22);
                    r[i+23]=av.lane(23);
                    r[i+24]=av.lane(24);
                    r[i+25]=av.lane(25);
                    r[i+26]=av.lane(26);
                    r[i+27]=av.lane(27);
                    r[i+28]=av.lane(28);
                    r[i+29]=av.lane(29);
                    r[i+30]=av.lane(30);
                    r[i+31]=av.lane(31);
                    r[i+32]=av.lane(32);
                    r[i+33]=av.lane(33);
                    r[i+34]=av.lane(34);
                    r[i+35]=av.lane(35);
                    r[i+36]=av.lane(36);
                    r[i+37]=av.lane(37);
                    r[i+38]=av.lane(38);
                    r[i+39]=av.lane(39);
                    r[i+40]=av.lane(40);
                    r[i+41]=av.lane(41);
                    r[i+42]=av.lane(42);
                    r[i+43]=av.lane(43);
                    r[i+44]=av.lane(44);
                    r[i+45]=av.lane(45);
                    r[i+46]=av.lane(46);
                    r[i+47]=av.lane(47);
                    r[i+48]=av.lane(48);
                    r[i+49]=av.lane(49);
                    r[i+50]=av.lane(50);
                    r[i+51]=av.lane(51);
                    r[i+52]=av.lane(52);
                    r[i+53]=av.lane(53);
                    r[i+54]=av.lane(54);
                    r[i+55]=av.lane(55);
                    r[i+56]=av.lane(56);
                    r[i+57]=av.lane(57);
                    r[i+58]=av.lane(58);
                    r[i+59]=av.lane(59);
                    r[i+60]=av.lane(60);
                    r[i+61]=av.lane(61);
                    r[i+62]=av.lane(62);
                    r[i+63]=av.lane(63);
                } else {
                    for (int j = 0; j < SPECIES.length(); j++) {
                        r[i+j]=av.lane(j);
                    }
                }
            }
        }

        assertArraysEquals(r, a, FloatMaxVectorTests::get);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void BroadcastFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = new float[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector.broadcast(SPECIES, a[i]).intoArray(r, i);
            }
        }

        assertBroadcastArraysEquals(r, a);
    }





    @Test(dataProvider = "floatUnaryOpProvider")
    static void ZeroFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = new float[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector.zero(SPECIES).intoArray(a, i);
            }
        }

        Assert.assertEquals(a, r);
    }




    static float[] sliceUnary(float[] a, int origin, int idx) {
        float[] res = new float[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else
                res[i] = (float)0;
        }
        return res;
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void sliceUnaryFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = new float[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.slice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, origin, FloatMaxVectorTests::sliceUnary);
    }
    static float[] sliceBinary(float[] a, float[] b, int origin, int idx) {
        float[] res = new float[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else {
                res[i] = b[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void sliceBinaryFloatMaxVectorTestsBinary(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = new float[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, origin, FloatMaxVectorTests::sliceBinary);
    }
    static float[] slice(float[] a, float[] b, int origin, boolean[] mask, int idx) {
        float[] res = new float[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = mask[i] ? a[idx+i+origin] : (float)0;
            else {
                res[i] = mask[i] ? b[idx+j] : (float)0;
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void sliceFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
    IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        float[] r = new float[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, origin, mask, FloatMaxVectorTests::slice);
    }
    static float[] unsliceUnary(float[] a, int origin, int idx) {
        float[] res = new float[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i < origin)
                res[i] = (float)0;
            else {
                res[i] = a[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void unsliceUnaryFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = new float[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.unslice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, origin, FloatMaxVectorTests::unsliceUnary);
    }
    static float[] unsliceBinary(float[] a, float[] b, int origin, int part, int idx) {
        float[] res = new float[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if (part == 0) {
                if (i < origin)
                    res[i] = b[idx+i];
                else {
                    res[i] = a[idx+j];
                    j++;
                }
            } else if (part == 1) {
                if (i < origin)
                    res[i] = a[idx+SPECIES.length()-origin+i];
                else {
                    res[i] = b[idx+origin+j];
                    j++;
                }
            }
        }
        return res;
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void unsliceBinaryFloatMaxVectorTestsBinary(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = new float[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        int part = (new java.util.Random()).nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, origin, part, FloatMaxVectorTests::unsliceBinary);
    }
    static float[] unslice(float[] a, float[] b, int origin, int part, boolean[] mask, int idx) {
        float[] res = new float[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = b[idx+i+origin];
            else {
                res[i] = b[idx+j];
                j++;
            }
        }
        for (int i = 0; i < SPECIES.length(); i++){
            res[i] = mask[i] ? a[idx+i] : res[i];
        }
        float[] res1 = new float[SPECIES.length()];
        if (part == 0) {
            for (int i = 0, j = 0; i < SPECIES.length(); i++){
                if (i < origin)
                    res1[i] = b[idx+i];
                else {
                   res1[i] = res[j];
                   j++;
                }
            }
        } else if (part == 1) {
            for (int i = 0, j = 0; i < SPECIES.length(); i++){
                if (i < origin)
                    res1[i] = res[SPECIES.length()-origin+i];
                else {
                    res1[i] = b[idx+origin+j];
                    j++;
                }
            }
        }
        return res1;
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void unsliceFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
    IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        float[] r = new float[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        int part = (new java.util.Random()).nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, origin, part, mask, FloatMaxVectorTests::unslice);
    }

    static float SIN(float a) {
        return (float)(Math.sin((double)a));
    }

    static float strictSIN(float a) {
        return (float)(StrictMath.sin((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void SINFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.SIN).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::SIN, FloatMaxVectorTests::strictSIN);
    }


    static float EXP(float a) {
        return (float)(Math.exp((double)a));
    }

    static float strictEXP(float a) {
        return (float)(StrictMath.exp((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void EXPFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.EXP).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::EXP, FloatMaxVectorTests::strictEXP);
    }


    static float LOG1P(float a) {
        return (float)(Math.log1p((double)a));
    }

    static float strictLOG1P(float a) {
        return (float)(StrictMath.log1p((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void LOG1PFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LOG1P).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::LOG1P, FloatMaxVectorTests::strictLOG1P);
    }


    static float LOG(float a) {
        return (float)(Math.log((double)a));
    }

    static float strictLOG(float a) {
        return (float)(StrictMath.log((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void LOGFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LOG).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::LOG, FloatMaxVectorTests::strictLOG);
    }


    static float LOG10(float a) {
        return (float)(Math.log10((double)a));
    }

    static float strictLOG10(float a) {
        return (float)(StrictMath.log10((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void LOG10FloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LOG10).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::LOG10, FloatMaxVectorTests::strictLOG10);
    }


    static float EXPM1(float a) {
        return (float)(Math.expm1((double)a));
    }

    static float strictEXPM1(float a) {
        return (float)(StrictMath.expm1((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void EXPM1FloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.EXPM1).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::EXPM1, FloatMaxVectorTests::strictEXPM1);
    }


    static float COS(float a) {
        return (float)(Math.cos((double)a));
    }

    static float strictCOS(float a) {
        return (float)(StrictMath.cos((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void COSFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.COS).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::COS, FloatMaxVectorTests::strictCOS);
    }


    static float TAN(float a) {
        return (float)(Math.tan((double)a));
    }

    static float strictTAN(float a) {
        return (float)(StrictMath.tan((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void TANFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.TAN).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::TAN, FloatMaxVectorTests::strictTAN);
    }


    static float SINH(float a) {
        return (float)(Math.sinh((double)a));
    }

    static float strictSINH(float a) {
        return (float)(StrictMath.sinh((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void SINHFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.SINH).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::SINH, FloatMaxVectorTests::strictSINH);
    }


    static float COSH(float a) {
        return (float)(Math.cosh((double)a));
    }

    static float strictCOSH(float a) {
        return (float)(StrictMath.cosh((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void COSHFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.COSH).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::COSH, FloatMaxVectorTests::strictCOSH);
    }


    static float TANH(float a) {
        return (float)(Math.tanh((double)a));
    }

    static float strictTANH(float a) {
        return (float)(StrictMath.tanh((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void TANHFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.TANH).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::TANH, FloatMaxVectorTests::strictTANH);
    }


    static float ASIN(float a) {
        return (float)(Math.asin((double)a));
    }

    static float strictASIN(float a) {
        return (float)(StrictMath.asin((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void ASINFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASIN).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::ASIN, FloatMaxVectorTests::strictASIN);
    }


    static float ACOS(float a) {
        return (float)(Math.acos((double)a));
    }

    static float strictACOS(float a) {
        return (float)(StrictMath.acos((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void ACOSFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ACOS).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::ACOS, FloatMaxVectorTests::strictACOS);
    }


    static float ATAN(float a) {
        return (float)(Math.atan((double)a));
    }

    static float strictATAN(float a) {
        return (float)(StrictMath.atan((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void ATANFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ATAN).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::ATAN, FloatMaxVectorTests::strictATAN);
    }


    static float CBRT(float a) {
        return (float)(Math.cbrt((double)a));
    }

    static float strictCBRT(float a) {
        return (float)(StrictMath.cbrt((double)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void CBRTFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.CBRT).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, FloatMaxVectorTests::CBRT, FloatMaxVectorTests::strictCBRT);
    }


    static float HYPOT(float a, float b) {
        return (float)(Math.hypot((double)a, (double)b));
    }

    static float strictHYPOT(float a, float b) {
        return (float)(StrictMath.hypot((double)a, (double)b));
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void HYPOTFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.HYPOT, bv).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, b, FloatMaxVectorTests::HYPOT, FloatMaxVectorTests::strictHYPOT);
    }



    static float POW(float a, float b) {
        return (float)(Math.pow((double)a, (double)b));
    }

    static float strictPOW(float a, float b) {
        return (float)(StrictMath.pow((double)a, (double)b));
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void POWFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.POW, bv).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, b, FloatMaxVectorTests::POW, FloatMaxVectorTests::strictPOW);
    }

    static float pow(float a, float b) {
        return (float)(Math.pow((double)a, (double)b));
    }

    static float strictpow(float a, float b) {
        return (float)(StrictMath.pow((double)a, (double)b));
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void powFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.pow(bv).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, b, FloatMaxVectorTests::pow, FloatMaxVectorTests::strictpow);
    }



    static float ATAN2(float a, float b) {
        return (float)(Math.atan2((double)a, (double)b));
    }

    static float strictATAN2(float a, float b) {
        return (float)(StrictMath.atan2((double)a, (double)b));
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void ATAN2FloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ATAN2, bv).intoArray(r, i);
            }
        }

        assertArraysEqualsWithinOneUlp(r, a, b, FloatMaxVectorTests::ATAN2, FloatMaxVectorTests::strictATAN2);
    }



    @Test(dataProvider = "floatBinaryOpProvider")
    static void POWFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.POW, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEqualsWithinOneUlp(r, a, b, FloatMaxVectorTests::POW, FloatMaxVectorTests::strictPOW);
    }

    @Test(dataProvider = "floatBinaryOpProvider")
    static void powFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.pow(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEqualsWithinOneUlp(r, a, b, FloatMaxVectorTests::pow, FloatMaxVectorTests::strictpow);
    }



    static float FMA(float a, float b, float c) {
        return (float)(Math.fma(a, b, c));
    }
    static float fma(float a, float b, float c) {
        return (float)(Math.fma(a, b, c));
    }


    @Test(dataProvider = "floatTernaryOpProvider")
    static void FMAFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb, IntFunction<float[]> fc) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                FloatVector cv = FloatVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.FMA, bv, cv).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, c, FloatMaxVectorTests::FMA);
    }
    @Test(dataProvider = "floatTernaryOpProvider")
    static void fmaFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb, IntFunction<float[]> fc) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            FloatVector cv = FloatVector.fromArray(SPECIES, c, i);
            av.fma(bv, cv).intoArray(r, i);
        }

        assertArraysEquals(r, a, b, c, FloatMaxVectorTests::fma);
    }


    @Test(dataProvider = "floatTernaryOpMaskProvider")
    static void FMAFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<float[]> fc, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
                FloatVector cv = FloatVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.FMA, bv, cv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, c, mask, FloatMaxVectorTests::FMA);
    }





    @Test(dataProvider = "floatTernaryOpProvider")
    static void FMAFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb, IntFunction<float[]> fc) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.FMA, bv, c[i]).intoArray(r, i);
        }
        assertBroadcastArraysEquals(r, a, b, c, FloatMaxVectorTests::FMA);
    }

    @Test(dataProvider = "floatTernaryOpProvider")
    static void FMAFloatMaxVectorTestsAltBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb, IntFunction<float[]> fc) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector cv = FloatVector.fromArray(SPECIES, c, i);
            av.lanewise(VectorOperators.FMA, b[i], cv).intoArray(r, i);
        }
        assertAltBroadcastArraysEquals(r, a, b, c, FloatMaxVectorTests::FMA);
    }


    @Test(dataProvider = "floatTernaryOpMaskProvider")
    static void FMAFloatMaxVectorTestsBroadcastMaskedSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<float[]> fc, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.FMA, bv, c[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(r, a, b, c, mask, FloatMaxVectorTests::FMA);
    }

    @Test(dataProvider = "floatTernaryOpMaskProvider")
    static void FMAFloatMaxVectorTestsAltBroadcastMaskedSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<float[]> fc, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector cv = FloatVector.fromArray(SPECIES, c, i);
            av.lanewise(VectorOperators.FMA, b[i], cv, vmask).intoArray(r, i);
        }

        assertAltBroadcastArraysEquals(r, a, b, c, mask, FloatMaxVectorTests::FMA);
    }




    @Test(dataProvider = "floatTernaryOpProvider")
    static void FMAFloatMaxVectorTestsDoubleBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb, IntFunction<float[]> fc) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.FMA, b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(r, a, b, c, FloatMaxVectorTests::FMA);
    }
    @Test(dataProvider = "floatTernaryOpProvider")
    static void fmaFloatMaxVectorTestsDoubleBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb, IntFunction<float[]> fc) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.fma(b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(r, a, b, c, FloatMaxVectorTests::fma);
    }


    @Test(dataProvider = "floatTernaryOpMaskProvider")
    static void FMAFloatMaxVectorTestsDoubleBroadcastMaskedSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<float[]> fc, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] c = fc.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.FMA, b[i], c[i], vmask).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(r, a, b, c, mask, FloatMaxVectorTests::FMA);
    }




    static float NEG(float a) {
        return (float)(-((float)a));
    }

    static float neg(float a) {
        return (float)(-((float)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void NEGFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, FloatMaxVectorTests::NEG);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void negFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.neg().intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, FloatMaxVectorTests::neg);
    }

    @Test(dataProvider = "floatUnaryOpMaskProvider")
    static void NEGMaskedFloatMaxVectorTests(IntFunction<float[]> fa,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, FloatMaxVectorTests::NEG);
    }

    static float ABS(float a) {
        return (float)(Math.abs((float)a));
    }

    static float abs(float a) {
        return (float)(Math.abs((float)a));
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void ABSFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, FloatMaxVectorTests::ABS);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void absFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.abs().intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, FloatMaxVectorTests::abs);
    }

    @Test(dataProvider = "floatUnaryOpMaskProvider")
    static void ABSMaskedFloatMaxVectorTests(IntFunction<float[]> fa,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, FloatMaxVectorTests::ABS);
    }








    static float SQRT(float a) {
        return (float)(Math.sqrt((double)a));
    }

    static float sqrt(float a) {
        return (float)(Math.sqrt((double)a));
    }



    @Test(dataProvider = "floatUnaryOpProvider")
    static void SQRTFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.SQRT).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, FloatMaxVectorTests::SQRT);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void sqrtFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.sqrt().intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, FloatMaxVectorTests::sqrt);
    }



    @Test(dataProvider = "floatUnaryOpMaskProvider")
    static void SQRTMaskedFloatMaxVectorTests(IntFunction<float[]> fa,
                                                IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.SQRT, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, mask, FloatMaxVectorTests::SQRT);
    }

    static float[] gather(float a[], int ix, int[] b, int iy) {
        float[] res = new float[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++) {
            int bi = iy + i;
            res[i] = a[b[bi] + ix];
        }
        return res;
    }

    @Test(dataProvider = "floatUnaryOpIndexProvider")
    static void gatherFloatMaxVectorTests(IntFunction<float[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        float[] a = fa.apply(SPECIES.length());
        int[] b    = fs.apply(a.length, SPECIES.length());
        float[] r = new float[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i, b, i);
                av.intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::gather);
    }
    static float[] gatherMasked(float a[], int ix, boolean[] mask, int[] b, int iy) {
        float[] res = new float[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++) {
            int bi = iy + i;
            if (mask[i]) {
              res[i] = a[b[bi] + ix];
            }
        }
        return res;
    }

    @Test(dataProvider = "floatUnaryMaskedOpIndexProvider")
    static void gatherMaskedFloatMaxVectorTests(IntFunction<float[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        int[] b    = fs.apply(a.length, SPECIES.length());
        float[] r = new float[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i, b, i, vmask);
                av.intoArray(r, i);
            }
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::gatherMasked);
    }

    static float[] scatter(float a[], int ix, int[] b, int iy) {
      float[] res = new float[SPECIES.length()];
      for (int i = 0; i < SPECIES.length(); i++) {
        int bi = iy + i;
        res[b[bi]] = a[i + ix];
      }
      return res;
    }

    @Test(dataProvider = "floatUnaryOpIndexProvider")
    static void scatterFloatMaxVectorTests(IntFunction<float[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        float[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        float[] r = new float[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i);
            }
        }

        assertArraysEquals(r, a, b, FloatMaxVectorTests::scatter);
    }

    static float[] scatterMasked(float r[], float a[], int ix, boolean[] mask, int[] b, int iy) {
      // First, gather r.
      float[] oldVal = gather(r, ix, b, iy);
      float[] newVal = new float[SPECIES.length()];

      // Second, blending it with a.
      for (int i = 0; i < SPECIES.length(); i++) {
        newVal[i] = blend(oldVal[i], a[i+ix], mask[i]);
      }

      // Third, scatter: copy old value of r, and scatter it manually.
      float[] res = Arrays.copyOfRange(r, ix, ix+SPECIES.length());
      for (int i = 0; i < SPECIES.length(); i++) {
        int bi = iy + i;
        res[b[bi]] = newVal[i];
      }

      return res;
    }

    @Test(dataProvider = "scatterMaskedOpIndexProvider")
    static void scatterMaskedFloatMaxVectorTests(IntFunction<float[]> fa, IntFunction<float[]> fb, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        float[] r = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i, vmask);
            }
        }

        assertArraysEquals(r, a, b, mask, FloatMaxVectorTests::scatterMasked);
    }


    @Test(dataProvider = "floatCompareOpProvider")
    static void ltFloatMaxVectorTestsBroadcastSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.lt(b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i]);
            }
        }
    }

    @Test(dataProvider = "floatCompareOpProvider")
    static void eqFloatMaxVectorTestsBroadcastMaskedSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            VectorMask<Float> mv = av.eq(b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i]);
            }
        }
    }

    @Test(dataProvider = "floattoIntUnaryOpProvider")
    static void toIntArrayFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            int[] r = av.toIntArray();
            assertArraysEquals(r, a, i);
        }
    }

    @Test(dataProvider = "floattoLongUnaryOpProvider")
    static void toLongArrayFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            long[] r = av.toLongArray();
            assertArraysEquals(r, a, i);
        }
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void toDoubleArrayFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            double[] r = av.toDoubleArray();
            assertArraysEquals(r, a, i);
        }
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void toStringFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            String str = av.toString();

            float subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            Assert.assertTrue(str.equals(Arrays.toString(subarr)), "at index " + i + ", string should be = " + Arrays.toString(subarr) + ", but is = " + str);
        }
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void hashCodeFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            int hash = av.hashCode();

            float subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            int expectedHash = Objects.hash(SPECIES, Arrays.hashCode(subarr));
            Assert.assertTrue(hash == expectedHash, "at index " + i + ", hash should be = " + expectedHash + ", but is = " + hash);
        }
    }


    static long ADDReduceLong(float[] a, int idx) {
        float res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res += a[i];
        }

        return (long)res;
    }

    static long ADDReduceAllLong(float[] a) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDReduceLong(a, i);
        }

        return res;
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void ADDReduceLongFloatMaxVectorTests(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        long[] r = lfr.apply(SPECIES.length());
        long ra = 0;

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            r[i] = av.reduceLanesToLong(VectorOperators.ADD);
        }

        ra = 0;
        for (int i = 0; i < a.length; i ++) {
            ra += r[i];
        }

        assertReductionLongArraysEquals(r, ra, a,
                FloatMaxVectorTests::ADDReduceLong, FloatMaxVectorTests::ADDReduceAllLong);
    }

    static long ADDReduceLongMasked(float[] a, int idx, boolean[] mask) {
        float res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res += a[i];
        }

        return (long)res;
    }

    static long ADDReduceAllLongMasked(float[] a, boolean[] mask) {
        long res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            res += ADDReduceLongMasked(a, i, mask);
        }

        return res;
    }

    @Test(dataProvider = "floatUnaryOpMaskProvider")
    static void ADDReduceLongFloatMaxVectorTestsMasked(IntFunction<float[]> fa, IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        long[] r = lfr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        long ra = 0;

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            r[i] = av.reduceLanesToLong(VectorOperators.ADD, vmask);
        }

        ra = 0;
        for (int i = 0; i < a.length; i ++) {
            ra += r[i];
        }

        assertReductionLongArraysEqualsMasked(r, ra, a, mask,
                FloatMaxVectorTests::ADDReduceLongMasked, FloatMaxVectorTests::ADDReduceAllLongMasked);
    }

    @Test(dataProvider = "floattoLongUnaryOpProvider")
    static void BroadcastLongFloatMaxVectorTestsSmokeTest(IntFunction<float[]> fa) {
        float[] a = fa.apply(SPECIES.length());
        float[] r = new float[a.length];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector.broadcast(SPECIES, (long)a[i]).intoArray(r, i);
        }
        assertBroadcastArraysEquals(r, a);
    }

    @Test(dataProvider = "floatBinaryOpMaskProvider")
    static void blendFloatMaxVectorTestsBroadcastLongSmokeTest(IntFunction<float[]> fa, IntFunction<float[]> fb,
                                          IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] b = fb.apply(SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                FloatVector av = FloatVector.fromArray(SPECIES, a, i);
                av.blend((long)b[i], vmask).intoArray(r, i);
            }
        }
        assertBroadcastLongArraysEquals(r, a, b, mask, FloatMaxVectorTests::blend);
    }


    @Test(dataProvider = "floatUnaryOpSelectFromProvider")
    static void SelectFromFloatMaxVectorTests(IntFunction<float[]> fa,
                                           BiFunction<Integer,Integer,float[]> fs) {
        float[] a = fa.apply(SPECIES.length());
        float[] order = fs.apply(a.length, SPECIES.length());
        float[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, order, i);
            bv.selectFrom(av).intoArray(r, i);
        }

        assertSelectFromArraysEquals(r, a, order, SPECIES.length());
    }

    @Test(dataProvider = "floatUnaryOpSelectFromMaskProvider")
    static void SelectFromFloatMaxVectorTestsMaskedSmokeTest(IntFunction<float[]> fa,
                                                           BiFunction<Integer,Integer,float[]> fs,
                                                           IntFunction<boolean[]> fm) {
        float[] a = fa.apply(SPECIES.length());
        float[] order = fs.apply(a.length, SPECIES.length());
        float[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Float> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            FloatVector av = FloatVector.fromArray(SPECIES, a, i);
            FloatVector bv = FloatVector.fromArray(SPECIES, order, i);
            bv.selectFrom(av, vmask).intoArray(r, i);
        }

        assertSelectFromArraysEquals(r, a, order, mask, SPECIES.length());
    }

    @Test(dataProvider = "shuffleProvider")
    static void shuffleMiscellaneousFloatMaxVectorTestsSmokeTest(BiFunction<Integer,Integer,int[]> fs) {
        int[] a = fs.apply(SPECIES.length() * BUFFER_REPS, SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var shuffle = VectorShuffle.fromArray(SPECIES, a, i);
            int hash = shuffle.hashCode();
            int length = shuffle.length();

            int subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            int expectedHash = Objects.hash(SPECIES, Arrays.hashCode(subarr));
            Assert.assertTrue(hash == expectedHash, "at index " + i + ", hash should be = " + expectedHash + ", but is = " + hash);
            Assert.assertEquals(length, SPECIES.length());
        }
    }

    @Test(dataProvider = "shuffleProvider")
    static void shuffleToStringFloatMaxVectorTestsSmokeTest(BiFunction<Integer,Integer,int[]> fs) {
        int[] a = fs.apply(SPECIES.length() * BUFFER_REPS, SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var shuffle = VectorShuffle.fromArray(SPECIES, a, i);
            String str = shuffle.toString();

            int subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            Assert.assertTrue(str.equals("Shuffle" + Arrays.toString(subarr)), "at index " +
                i + ", string should be = " + Arrays.toString(subarr) + ", but is = " + str);
        }
    }

    @Test(dataProvider = "shuffleCompareOpProvider")
    static void shuffleEqualsFloatMaxVectorTestsSmokeTest(BiFunction<Integer,Integer,int[]> fa, BiFunction<Integer,Integer,int[]> fb) {
        int[] a = fa.apply(SPECIES.length() * BUFFER_REPS, SPECIES.length());
        int[] b = fb.apply(SPECIES.length() * BUFFER_REPS, SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = VectorShuffle.fromArray(SPECIES, a, i);
            var bv = VectorShuffle.fromArray(SPECIES, b, i);
            boolean eq = av.equals(bv);
            int to = i + SPECIES.length();
            Assert.assertEquals(eq, Arrays.equals(a, i, to, b, i, to));
        }
    }

    @Test(dataProvider = "maskCompareOpProvider")
    static void maskEqualsFloatMaxVectorTestsSmokeTest(IntFunction<boolean[]> fa, IntFunction<boolean[]> fb) {
        boolean[] a = fa.apply(SPECIES.length());
        boolean[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = SPECIES.loadMask(a, i);
            var bv = SPECIES.loadMask(b, i);
            boolean equals = av.equals(bv);
            int to = i + SPECIES.length();
            Assert.assertEquals(equals, Arrays.equals(a, i, to, b, i, to));
        }
    }

    static boolean beq(boolean a, boolean b) {
        return (a == b);
    }

    @Test(dataProvider = "maskCompareOpProvider")
    static void maskEqFloatMaxVectorTestsSmokeTest(IntFunction<boolean[]> fa, IntFunction<boolean[]> fb) {
        boolean[] a = fa.apply(SPECIES.length());
        boolean[] b = fb.apply(SPECIES.length());
        boolean[] r = new boolean[a.length];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var av = SPECIES.loadMask(a, i);
            var bv = SPECIES.loadMask(b, i);
            var cv = av.eq(bv);
            cv.intoArray(r, i);
        }
        assertArraysEquals(r, a, b, FloatMaxVectorTests::beq);
    }

    @Test(dataProvider = "maskProvider")
    static void maskHashCodeFloatMaxVectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        boolean[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var vmask = SPECIES.loadMask(a, i);
            int hash = vmask.hashCode();

            boolean subarr[] = Arrays.copyOfRange(a, i, i + SPECIES.length());
            int expectedHash = Objects.hash(SPECIES, Arrays.hashCode(subarr));
            Assert.assertTrue(hash == expectedHash, "at index " + i + ", hash should be = " + expectedHash + ", but is = " + hash);
        }
    }

    @Test(dataProvider = "maskProvider")
    static void maskTrueCountFloatMaxVectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        boolean[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var vmask = SPECIES.loadMask(a, i);
            int tcount = vmask.trueCount();
            int expectedTcount = 0;
            for (int j = i; j < i + SPECIES.length(); j++) {
                expectedTcount += a[j] ? 1 : 0;
            }
            Assert.assertTrue(tcount == expectedTcount, "at index " + i + ", trueCount should be = " + expectedTcount + ", but is = " + tcount);
        }
    }

    @Test(dataProvider = "maskProvider")
    static void maskLastTrueFloatMaxVectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        boolean[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var vmask = SPECIES.loadMask(a, i);
            int ltrue = vmask.lastTrue();
            int j = i + SPECIES.length() - 1;
            for (; j >= i; j--) {
                if (a[j]) break;
            }
            int expectedLtrue = j - i;

            Assert.assertTrue(ltrue == expectedLtrue, "at index " + i +
                ", lastTrue should be = " + expectedLtrue + ", but is = " + ltrue);
        }
    }

    @Test(dataProvider = "maskProvider")
    static void maskFirstTrueFloatMaxVectorTestsSmokeTest(IntFunction<boolean[]> fa) {
        boolean[] a = fa.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            var vmask = SPECIES.loadMask(a, i);
            int ftrue = vmask.firstTrue();
            int j = i;
            for (; j < i + SPECIES.length() ; j++) {
                if (a[j]) break;
            }
            int expectedFtrue = j - i;

            Assert.assertTrue(ftrue == expectedFtrue, "at index " + i +
                ", firstTrue should be = " + expectedFtrue + ", but is = " + ftrue);
        }
    }


    @DataProvider
    public static Object[][] offsetProvider() {
        return new Object[][]{
                {0},
                {-1},
                {+1},
                {+2},
                {-2},
        };
    }

    @Test(dataProvider = "offsetProvider")
    static void indexInRangeFloatMaxVectorTestsSmokeTest(int offset) {
        int limit = SPECIES.length() * BUFFER_REPS;
        for (int i = 0; i < limit; i += SPECIES.length()) {
            var actualMask = SPECIES.indexInRange(i + offset, limit);
            var expectedMask = SPECIES.maskAll(true).indexInRange(i + offset, limit);
            assert(actualMask.equals(expectedMask));
            for (int j = 0; j < SPECIES.length(); j++)  {
                int index = i + j + offset;
                Assert.assertEquals(actualMask.laneIsSet(j), index >= 0 && index < limit);
            }
        }
    }

    @DataProvider
    public static Object[][] lengthProvider() {
        return new Object[][]{
                {0},
                {1},
                {32},
                {37},
                {1024},
                {1024+1},
                {1024+5},
        };
    }

    @Test(dataProvider = "lengthProvider")
    static void loopBoundFloatMaxVectorTestsSmokeTest(int length) {
        int actualLoopBound = SPECIES.loopBound(length);
        int expectedLoopBound = length - Math.floorMod(length, SPECIES.length());
        Assert.assertEquals(actualLoopBound, expectedLoopBound);
    }

    @Test
    static void ElementSizeFloatMaxVectorTestsSmokeTest() {
        FloatVector av = FloatVector.zero(SPECIES);
        int elsize = av.elementSize();
        Assert.assertEquals(elsize, Float.SIZE);
    }

    @Test
    static void VectorShapeFloatMaxVectorTestsSmokeTest() {
        FloatVector av = FloatVector.zero(SPECIES);
        VectorShape vsh = av.shape();
        assert(vsh.equals(VectorShape.S_Max_BIT));
    }

    @Test
    static void ShapeWithLanesFloatMaxVectorTestsSmokeTest() {
        FloatVector av = FloatVector.zero(SPECIES);
        VectorShape vsh = av.shape();
        VectorSpecies species = vsh.withLanes(float.class);
        assert(species.equals(SPECIES));
    }

    @Test
    static void ElementTypeFloatMaxVectorTestsSmokeTest() {
        FloatVector av = FloatVector.zero(SPECIES);
        assert(av.species().elementType() == float.class);
    }

    @Test
    static void SpeciesElementSizeFloatMaxVectorTestsSmokeTest() {
        FloatVector av = FloatVector.zero(SPECIES);
        assert(av.species().elementSize() == Float.SIZE);
    }

    @Test
    static void VectorTypeFloatMaxVectorTestsSmokeTest() {
        FloatVector av = FloatVector.zero(SPECIES);
        assert(av.species().vectorType() == av.getClass());
    }

    @Test
    static void WithLanesFloatMaxVectorTestsSmokeTest() {
        FloatVector av = FloatVector.zero(SPECIES);
        VectorSpecies species = av.species().withLanes(float.class);
        assert(species.equals(SPECIES));
    }

    @Test
    static void WithShapeFloatMaxVectorTestsSmokeTest() {
        FloatVector av = FloatVector.zero(SPECIES);
        VectorShape vsh = av.shape();
        VectorSpecies species = av.species().withShape(vsh);
        assert(species.equals(SPECIES));
    }
}

