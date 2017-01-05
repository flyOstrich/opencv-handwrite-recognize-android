package com.example.handwrite;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.allere.handwriterecognize.FileOperator;
import com.allere.handwriterecognize.HandWriteRecognizer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.opencv.android.Utils.bitmapToMat;

/**
 * Created by allere on 16/12/21.
 */
@RunWith(AndroidJUnit4.class)
public class HandWriteRecognizeTest {
    @Test
    public void TestRecognizeBase64Str() throws Exception {
        //包含1，2，3，4的base64图像字符串
        Context appContext = InstrumentationRegistry.getTargetContext();


//        HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
//        handWriteRecognizer.recognizeImg(base64FormatStr, appContext);

    }

    @Test
    public void TestReadImgMatListAndTrain() {
        final Context appContext = InstrumentationRegistry.getInstrumentation().getContext();


        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(appContext) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        try {
                            FileOperator optr = new FileOperator(appContext);
                            HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
                            //train
                            String[] files = optr.getAssetFileNames(FileOperator.TRAIN_IMAGES_DIR);
                            Map res = optr.ReadFilesMat(files, FileOperator.TRAIN_IMAGES_DIR);
                            handWriteRecognizer.trainFromMat((Mat[]) res.get("images"), (int[]) res.get("labels"), HandWriteRecognizer.getSvmModelFilePath(appContext));

                        } catch (Exception e) {
                            Log.e("InstrumentTest", e.getMessage());
                        }
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, appContext, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


    }

    @Test
    public void TestPredictWithSvmModelFile() {
        final Context appContext = InstrumentationRegistry.getInstrumentation().getContext();
        final String base64FormatStr ="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAWgAAAEsCAYAAADuLCmvAAAgAElEQVR4Xu2dPYwWxxnHF9lKIn9AoihJATRRZImDHnyKFKU4sOQSDqSUcBRRGj66FHz1+CjSHdBzhp6PMgq+6/no0gBSXCQKjpQ4kiUn/9ce2Nt3Zt+d3dmPmfmNdDqb253d+T2z/5195plndhVF8e3/fygQgAAEIDAxArsQ6IlZhNuBAAQg8D0BBJquAAEIQGCiBBDoiRqG24IABCCAQNMHIAABCEyUAAI9UcNwWxCAAAQQaPoABCAAgYkSQKAnahhuCwIQgAACTR+AAAQgMFECCPREDcNtDUdg9+7dxYULF4pDhw4Vjx8/Lm7evFl89dVXw90AV4KAgwACTdfInsCNGzeK1dXVNxy++OKL4sSJE9lzAcD4BBDo8W3AHYxM4NWrV3N38PTp0zejaI2qHzx4UOjfKBAYkgACPSRtrjVJAtvb28W+ffsW3tuLFy+Kzc3N4vPPPy/03xQI9E0Age6bMPVPnsDHH39c3L171+s+NZqWWGtkjVh7oeNgDwIItAcsDk2XwMmTJ4v19fVWDdzY2Cg+++wzJhZb0eOkOgIINP0DAt8T0Ej64sWLhX63KRLqK1eutDmVcyBgJYBA0zEgUCGgsLuDBw/O/nX//v3FJ598Uhw7dqwRp/Pnz89cH7EWtb3vEMMhrhEr/+p9I9CpWLJhO5qODl++fIlvtcTUCLVcIUtLS7W0YxRptevq1auFxFNhhqdPnw4u1HrpyY2k33oJKJSRyJj6BxeBbihsUz9MnV6RCPotMdGP/l+/2xY9RPfv35/5V5kIe0vRiLUWt0jQqiU28VF7tra2djRDNr9+/XrbrmM97/bt2zu+RNSnjhw5EvQaqVWGQEdoUYmwRsJGjJuOirs0Ff/qPD0Jm6I/bCF6MS12kQvn1q1bOxrYx/0/f/587oW2d+/eLt0y+XMR6IhMLEHWg9RlVNyluVoCffny5S5VJHeubKKViDa3hz7hJXRTL5oY1ddAufQxgrYtCEKgcXFM/flofH/6DB1LnM1N6pMUd8e8yR49ejQn0rG80PQVUP0K68OPjkA3ftTfHMgI2p/ZaGfYOrjvzTQd0WlkaPOvxjIq9OXS9fih3ARd79N2vs31cPTo0eATeAi0v/UQaH9mo51hG+mYm9HElGbE9aMIjCdPnsxmytvOkkuc7927NzcqRKDt5retRuzDjxu68+lF/PDhwx3Vqt8cOHAg9KUKBNofabICrY73pz/9qfj1r39d7Nq1q/jnP/9ZfPrpp4XyLsRaJJrXrl2bTUoZ8VUin3Jin5Bts70QEOi0BNq2grKPF4v6rkbq5dLXiyDkMzB2XUkKtDqDhPhXv/rVTJxN+etf/1p89NFHYzOP5voIdHNTxTqCrqZaVYv7mCCMlU/zHtDPkUkK9NmzZ2dLbn/xi1/soPb69evipz/9aT8kE6wVgW5u1FgFyDbxfObMmVn8e8hiixRRVsBz586FvExydSUp0CZ9pMT43XfffWO0v/zlL8VvfvOb5IzYV4MQ6GZkXeGPynSnFXlTLa4sfvI/h17uPVSkyFRZt72vJAXaTEa88847xYcffljot0T7d7/7nXWioi281M9DoL/LxaE8HLaIFtlff5cf11b0FacFPlMtttFzXy+VoSJFpsq67X0lLdBlKATE+3eR3AVaAit3WZuiEejhw4eDj0Tb3IvtHJvLQcf14d6wjdSZIGxmyeQEOlZfYDNzDXtUzgJt5jHaEJ96Lg4ziV79KugjekP8lIRpbW1tB0r8z816FgLdjFOWR9k+gXMJs2u7avPZs2czv/OUV1u6fM99rRJVnLVJ32oepD5WKqb4kCYn0LaRT19+tRQ7hGmTLcOZ/paLq8h31aZGzVraHToDXB99zCbQfT0jtvhntcn1MpC/X32PrcS+s3xyAm37nOojrrOPB2dKddp8lH19Ak+p3eZebLk1zN80OtYnuilaKKQFQ6EjH/riMqQb0LYEXl8ZKysrc81Trmgz4SqWSswV8+YHIeyXnEDbHqxcPstDdAjV4fJRTj0qIVT7VY9NWMr162Wlz/QpuzJcPIYUaNuAyZZEyvXFlrsrJDmBtn2a9hHXGVIMplZXeSRTvrfcODbZSFYuDX2hxVSGFGib/9kWKWLLCWKY5izSSQm0reMpcZDCnSjNCLhEKZbUmc1a2fwojaT1wnLFQaumvraIan6Xfke6wt762ILKZ8BUlwxM7g71wdxKUgLNBGG37usaxUw9prdbqxefLXGWT74aKlY+U0ujNTKMobjcCbKzRquhlnn7DphcGRQNU/mjdX85laQEeqjELyl2ED0c+hy1bQiQ8ydm2dYSHPUx2xZXOi4mAbE9K6at8qtLpBVJ0TR/uO2ZaJN/Q/1Qexe6tnGL7Wulq1YkJdBMELbvDi7XBgsKdjKVgOhLrbpFlDmqj5V47a3qPnPRaLV8pskzrt8SyKY5xqubxKrOphPNdS8QXV/umFiiZrrYLxmBHjr4vgv0qZ2rUbP2OqwuJlA41PHjx7N4EHxt4upvMe1U7SPSVT5NcpD/9re/Ld57770dp/75z39u3J/qNkMWZ+36krpIJyPQtjcuE4SLZUcjQX2K2grhifX8XF8dsYyi1TqJtJ4dLRAJWZSH/ec///lclV9++WWwy+TwdZeMQJPOsHm/10OpB1Kf6tVRc7mW3MLqmhN8e6TtM16jS43uYioarepFXTdq9WnPD37wg+InP/nJjlO++eab4u9//7tPNbXH5rBwKmmBZgS4s39LmLVwwJUes3x0rmF1vurhcnXEOrGqPqLQwuXl5ZlYuyZEF3H64IMPivfff3/HYV9//XWhTTNCFUbQoUgOUE/OmdcW4dUo2YyY6+J5TT2I8yKiO/9u63upjO7UX9R/jGC7dnuvEvvxj39c/PCHP9zxz//617+Kf//7335wHUfnMj/CCDpIdxm3EuOy0ENUDpPz/VzVDPudO3eSn3gJba3URtFN+CzqW/Jr//KXv9xR1R//+MfZbvNdS5fd6rtee+jzEeihiQe+nib55EtuMjJ2XVqjPa3Uaho+FbgJSVRn2zEkpoiO0EbwWUEY+top1YdAR2pNCbI+resm+eqaZtJjanFFjAl/pmY2V0RHLulZy/awrUgloqpdj0Wg23Eb7SwJs5Yctx01S5glykryk3oM6ZBGGjIB0ZDtanMtWybAVHzybXh0OQeB7kJv4HM1MtGo2dedodGLWQWGj7kfoyHQb7nalniTk71dv0Og23Eb/Kwm4qywIwmwKU1Wew3ekEQviEC/NawtNjzWsMOxuysCPbYFGly/iTjLj6yR8hClnExniOvFcA3bqLGvbaSmzoOQ13AWQqDDseytpro8ub1dtEHF8mFvbGzM8vTm6M+Wq0lRNKdOnbK6nXL9rEegGzw8DQ9BoBuCGvMw3w1Mh77Xclyq9uaLbYcRX16rq6uzhRuaDHPNB4iJlnvnGCFj6685RrP49ivb8Qh0CIo912GLse35kp2qj2kloomKkeA2KVr6bMuZXT0319GzOCDQTXpSs2MQ6GacRj3K5t8c9YYWXFyjRyVaiqHU5R2uu3+10TV6ziFHRB0bBDpcz09aoGNK+7jIpIoS0ChPwhBiueyi67n+vmfPntmnvT7zXSWmmNdQ7iPZRe1WjHmoLaPa2mjM82yLVGJ6YY/JLjsXR+576fXZ2fSZr5G9kjCVR5Ji3sfmo321xbYLj++1NGK+dOlSlhOlVVa2FZUxvbB9bd/38cmMoF2fqk232OkbdMr1lxPndNnDbgxGTUIYbfelbGoS5dja2zdj9gUNSzgZgdYoTqOhav5aRtFhO0yqtS3KzqZ2y70jQVdkhlwZlHkCW1tbc5OoKbkah7Z5MgItcK54YRL3D92tuF6OBGz+Z3EgxK59b0hKoG1JWsii1b5zcCYEfAgogZdciuWC/9mH4PyxSQm0mqdJCnWUpaWlQkttlbWNPMfdOglnQ6AJAVsODuaAmpBzH5OcQHfDwdkQgEBbAg8fPpzLT457sS3N785DoLvx42wIQOD/BPA/99MNEOh+uFIrBLIi4JqgZ4KwWzdAoLvx42wIZE/ANXrOfcl7iI6BQIegSB0QyJiAa1dz5WPJMQ1tyK6AQIekSV0QyJQACZL6MTwC3Q9XaoVANgTW19dn4a3Vgv+5exdAoLszpAYIZEvAlhxJMJSrZGVlJVsuoRqOQIciST0QyJCAK1d5zhsWhOwGCHRImtQFgcwI2NIraPR8/PhxJggD9AUEOgBEqoBAzgSUWkGujtevX8/Sr2ojYaI3wvQIBDoMR2qBAAQgEJwAAh0cKRVCAAIQCEMAgQ7DkVogAAEIBCeAQAdHSoUQgAAEwhBAoMNwpBYIQAACwQkg0MGRUiEEIACBMAQQ6DAcqQUCEIBAcAIIdHCkVAgBCEAgDAEEOgxHaoEABCAQnAACHRwpFUIAAhAIQwCBDsORWiAAAQgEJ4BAB0dKhRCAAATCEECgw3CkFghAAALBCSDQwZFSoQ8BbTi6trZW7N69u9Amo/fv3/c5PdtjL1y4UCwvLxcvXrwoHj9+PEuQ//Tp02x5pNpwBDpVy0bQLony9vb2TJxNOXHixCxlJcVN4OrVq7OXWrUoxafEWkIthnCMvxch0PHbMNoW2HaDvnnzZnH58uVo2zTEjT9//nzHS63umhJpI9ovX75klD2EgQJeA4EOCJOq/Ago0fuVK1d2nCQ3x7lz5/wqyuzoR48eFUtLS51aHXJ0rUT9ck09ePCARP2drDJ/MgIdGGjT6vbv3z97yDSKPHTo0MyXqH3c9DuXYvtUZy+7xdaX3/7u3buNR9GLawx3hIQasQ7HE4EOx9JZk3yseqgkxvqtHwl0tch3ePTo0QHuaBqXePjw4YxFuZw5c4aJwobmUX/SRKF+62dqRf1ZfvEcBx+hbIFAe5A0Qmt+61QJzJ49e6y1lI9replcJsnERr7Uajly5EhWXxFN+0WT49QXJdhmMLBv374mpw1yjIRafZtIEz/cyQq0RqgKRdJvTZLo09mnSEDKI17XqNenzibH5iJQFy9enNmnXBQqtrKy0gQTxzQgoL5f7cPliJkGVQQ9RD7q06dPB60z9cqSFGhtBb++vr7DR7fIt2k+E+tcEH13hvPnzxebm5t9X2b0+iUccm9UxWKRjUa/8YRuIKRLRM+bfpqM2OXCYxTdvCMlJ9DqKLdu3ZojoFlrfWKpVEcVVT9oc3zdjtSIsRy3mssEoSa4bAKRy9dDt14z3bP17MmudWLNKNrPfkkJdHl2+5133inKP19++WXxj3/8wzo554es3dF6QZg41CdPnmS7iODkyZOzr5tqYfTcrl9N9Sx9HcmNZVtQs3fv3qne9uTuKwmBPnbs2OzN/fvf/342Yffuu+/uAP3tt9/OxPmbb77pbACNehX3aeJINeqtG/mamezOF06gAtvKQTVLLy75njWRREmLwKtXr+YahEA3t3G0Ai1B1mhMn1N68CXMP/rRj+Za/vXXX88efIm0bym7ICTCIYP7fe8lhePlepK9qiWXyJUUbOjbBgTal9jO46MTaInx7du3d/gwXeKsEbNGzk3E2bgfJMI5uyC6dSf32bZl3Toan2RfxKdRLwLdzQ7RCfSNGzeK1dXVN61+7733ig8//HCOQp1bw4ix3A9mko7P624dadHZW1tbc/5/MT98+DCujUXwIv47At3NeNEJdDkCYNeuXcXPfvazQr+r5W9/+1shUZAISIiNrxg3RbcO0+ZsW8yz6sklrLANs1TOQaC7WTI6gS4/7B988EHx/vvvz1wY//3vfwv5m/Xff/jDH7KIJ+5m+mHOdsU8l8Meh7kTrjIGAQS6G/XoBFrNVZKdTz/9tPjoo49mkRn/+c9/3lAgG1q3DhH6bNfEIDHPoUlPrz7Xcn6iOJrbKkqBVvNsqSr17zz4zY3f95GuiUFinvsmP436bfZnOb+fbaIVaNtqNB58P+P3fbRtYpCY576pT6d+29wDX7h+9olWoG2+LUbPfsbv82jXikHSifZJfVp1KxxWi8jKRRs0bGxsTOtGJ3w3UQq0lnQr2U65aGSmkC3K+ARcKwaZGBzfNkPege0rl0VJfhaIUqCrsdBqMgse/Azf59GusDoezj6pT69um4uLbHZ+dopSoG3uDWJq/Qzf19GusDp8j30Rn269hNh1t00yAo3/uXtnCFEDYXUhKKZRBwLd3Y5RCvT29vZccnA+n7t3hq41uMLqbt68WVy+fLlr9ZwfGQEEurvBohRoJh+6G76PGsi30QfVOOtkIj+M3RDoMByzr8X2QAoKYXV5dg1bmCVRPP59IRmBZpLQ3/ghz9Dy++ruGTyQIQnHVZctkoeFZP42jFKgbWLAtu7+xg95hs29wUszJOG46rK5IekP/jaMUqBdn9MSaXWC+/fv+5PgjNYEXHlRDhw4QK7n1lTjPpGVvmHsF6VAq+m2xSoGiZaS6nOKJPxhOkldLa5Vg00WDinqY9++fY028jV5vNnjsX+bdr2CtjVTuGW56FnUC5viRyBagZYw3Lt3r1haWrK2WB3i0qVLhRZIUPoj4Fo1aFsxpi8f2Wt5efnNXpJt7kxira8kvQTqNuxtUzfndCdgGzyxUKkd12gFWs1dJNI6RiMuxeCyk0q7DtJm9GwEVPYRf/2WkGuVYeii+jc3N2f21X9Txifw/Pnzmc3LhWiednaJWqCNSOuNXc2aVcWhB1j+aUZc7TqK7SzX6DncFfxqkm0l1hqtYWc/dqGOti1Wwr3Rnm70Am2arrhLRXdU39xVNHqANaLGP92+0+hMjYYVuTHVwjzEOJYhB3RY7skItBlNX7t2bceu3zZcEmfzAIfFmU9ttjAqcV30giwT0vFN/cgame3Zs8c55+CyM1E9w/ZJckCH5Z2UQBs0epj1JtfvuqLPYCUQJyzPr1O5wuoUOaOR9erqqrNCuZqMv7gNd9WvKAF9MbkmiKsX18tYdqb0T4A0DGEZJynQZbeHhFqhXHUF/3TzTuUKqytvZSXxNBOCZuSrCbw7d+4EnaxVVIgiQjT/sOhlrOsroRaurea2bnOkNtKQXcqFHNBtSH53TtICbbBIpLUMue7zWw+ufNPyUVPcBFyj57GzCcq2urc6O+uLSdEERHv018PJYBeWbRYCLWQm1KuaL6KKc2yhCWve8LXZltlPKcZVdq6L6iElQPg+Ua4RgQ7LNxuBNtj06b2+vu78JNYoS59kfArbO1osPkbXSF+tkm1lY0LxwoqJakOgwzLNTqANPvksNdKy+adJMO/uZLEItFogX6ju1+bakptDIk0JSwCBDsszW4E2bg+FBdkmmOSLVogWZSeBmARad64vJtnYFvFB+suwvVsvQq0irJa9e/eGvVBGtWUt0OYBdi244AGefxJiE2jzIn706JH1a4kIg3BqZ1ukwirCbnyzF2jhcy1ZpnOlIdDG3aEQsGoh/K6bgJTPtr28pzSBHK6lw9WEQH/P2hadoD/xeRa3i6N8964XMTu/hBEcckCH4ViuBYEu0WCCY3EHi9HFUW6VXB34oxfb2fcI/M++xJodj0Aj0M16yvdHxS7QdZEdR44cIfTOqze8Pdj2daLVpYcPH25ZI6eJAAKNQHs9CbELtBprS4mpf8df6tUVdhxsywENz/Y8zZkINALt1Ytsu2XEONHm8kezj6JXd5gd7Eo9yxeJP8vqGb0LtD4pzfJqLQCZch4EfNCLO5QSIWklZrVoou369etBkyEtvptuR9hGfew83Y4pz047bovO6lWg9WZVaFN5JddU36q2ncKZ3bd3n+3tbWeGQC2fVhpRk1Z0ykvmp55XZNHDO5W/u0bQREB1t1CvAm3LhzDVxR8232qTnam7myC+GmwvM1crJNj6ajI/z549m8xEnM0XzRJw//5IiKo/s6ZnDC7QU5s40OheHUyf7tXC5667G7lcHU07nkbYT548KTTTb8R7jNE2n+ZNLeY+jgUq3Rm6auhVoF2z5foElj96zJ22dW9K9H7q1ClrMp1yAvr+8Mdds0bSynOxaEOEpq2UQEusX79+Pftt/r98fug+g0A3tY77ONsIeqpfyt1bO2wNvQq0mmJ7AEwTx9jYU9sxaQbf7Pjhwk1e6OYdUaNpbUO1aGf15jWOeyS+03r+2j5M7su6omP0fFO6EehdoG2bSJZvech9ARV9YHNl4Nro1onM2XIXaQsqibVG1033DAxz9XC1INBulk1dWyShCtMfexfophNK+nTVnnXyUYcscmUozE+isaiw7dUiQv5/F399ragf6GfR3oH+Vwh7Bgmy6nm64sfLZzG5Hq5P9i7QulWJo/xUTXyVekDko9aPDN2maCSnz219hlU3sHTVpxfDpUuX2EmlDXDPcyTY6gsabcs++v+pjLbZrKHemHqWb9265TyI58jzYVhw+CACXb4HieaFCxdqN3AN20R3bXoBmJH7GBEEQ7UzlusY4dZvM+qu7obS1whc9tcmDVpsQ1+o7zF6huXqKL9U9RyJH5suh33aBhdo3f6ifQHDNtFeGyF0Q1DmGhCAQBcCowi0ueG6fQG7NMp1rkLnzIg5dLhWH/dLnRCAQN4ERhXoslArHll+Y9sGn11NFGOeiK5t5nwIQCB+ApMQ6DJG+bbM5FGXiSOJshY7yCc25QRN8XchWgABCPRFYHIC3VdDqRcCEIBAbAQQ6Ngsxv1CAALZEECgszE1DYUABGIjgEDHZjHuFwIQyIYAAp2NqWkoBCAQGwEEOjaLcb8QgEA2BBDobExNQyEAgdgIINCxWYz7hQAEsiGAQGdjahoKAQjERgCBjs1i3C8EIJANAQQ6G1PTUAhAIDYCCHRsFuN+IQCBbAgg0NmYmoZCAAKxEUCgY7MY9wsBCGRDAIHOxtQ0FAIQiI0AAh2bxbhfCEAgGwIIdDampqEQgEBsBBDo2CzG/UIAAtkQQKCzMTUNhQAEYiOAQMdmMe4XAhDIhgACnY2paSgEIBAbAQQ6NotxvxCAQDYEEOhsTE1DIQCB2Agg0LFZjPuFAASyIYBAZ2NqGgoBCMRGAIGOzWLcLwQgkA0BBDobU9NQCEAgNgIIdGwW434hAIFsCCDQ2ZiahkIAArERQKBjsxj3CwEIZEMAgc7G1DQUAhCIjQACHZvFuF8IQCAbAgh0NqamoRCAQGwEJi3Qq6urxSeffFLs2bPHyvXx48ezf//iiy+Kly9fFi9evIiNP/cLAQhAwElgsgK9vr5enDx50tt0EusnT57MBPvp06cz8aZAAAIQiJHAJAX6448/Lu7evRuMp4Ta/Eiw9d8UCEAAAlMnMEmBvnr1arG2ttYrOwm1XCQS62fPnuEe6ZV2t8r3799f7Nu3r9CLe/fu3cWhQ4dmvw8ePOisWO6uK1euFPfv3+92cc6GwIgEJinQGj3rYRy66KGWa0RFbpKvvvpqJtwScEbdw1hDoru0tDQTXwlxl34g+x09epSX7zCm4yo9EJikQF+8eLG4cOHCjuZ+/vnnxZ07d978myYO9RCXR1c98HlTpRmRadQt37hGcLonJia7U5cIHzt2bDYhLHuGLCdOnGAeIiRQ6hqUQDQC/dlnnxXXr1+vhWPEenl5eSbe+tGncciikbT5tGaE5kdW3OS+6jIq9rtiUSDQvsQ4fkoEkhJoG1jjq5RoSxgkEvq3UKXJiyPUtWKuR9wfPnw4eBMQ6MGRc8GABJIXaBsrM7ou/24r2gj04t4ozppXaMtYVyjHuusr5vXr17N5AX3FmGKbu0CgF9uHI6ZLIEuBdpmjPLrWiFul6guvnotAuzu3vlgUjSPfsk+RGJtwSIlwEz+/6yWAQPuQ59ipEUCgF1hE4qJJS43+NFpThEG5INB2gD6x7GZRkULi9FMeFTd5YGQjLWyyjdCPHDnSSOCbXIdjIDA0AQTag7gtugSBngcooZS/2RWRobDF48ePewtx9Uq6joS5boTOCNqjg3Po5Agg0B4mQaCbwTp79uxskYithBLnulFz+boIdDObcdQ0CSDQHnZBoJvB0ujZtsrvwYMHxblz5zqPnCX+egk0KQh0E0ocM1UCkxRojY5u3bq1g5ke7tOnT4/KEYFejF9uja2trbkDQ/iC5dJQpEbdEu/qhRHoxTbjiOkSmKRA22JmNXF04MCBUUlKeKp+VY3mNjY2Rr2vKV3cNjmoiAwJpU/Rak0taukSmqfrIdA+1Dl2agQmKdCC9Pz587mH8/z588Xm5uYoDF0LLZTrgTwdb00S4uXqGoW3MTwC3YYa50yFwGQF+saNG4US9peLRtGHDx/u7MNsA982MtSE18rKSpvqkj7n1atXc+3bu3dv4zb7hOgtqhSBXkSIv0+ZwGQF2vWQKk72zJkzgzLVqFDhXFXfJwJtN0NXgdYIWhONXd0buDgGfUy4WA8EJivQaqttFK1/l5tD7o6hiisqQdnsFJVA2UnAJtC+riCzQKi6MMikg22aBIsRNL0zZgKTFmiNoLa3t60jKU08KarDd9WZr7FcI3ldVw8//ud5orb5g1C8XKNr1W8bcWtiue8+4tunOB4CTQlMWqDVCFvInWmcxFEj6b5E0hXWFUpsmhoptuNs4Yhqg7gpp7fJtdFGOF277ehladsmzcf3HRtn7jd9ApMXaJlAIVfyAduKHvLLly8Hj+6oi7kltK7+wRC7e/fuzeUtqZ4lV5WWyjdJhqRzXV9UN2/enPWBrr7v9B93WhgbgSgEepFI6+962PWQthmVVY1WJ85jRpLE1LmairTaZPaHNKNs1xeR3E0anZdL2R4IdEw9hHttQiAagVZj9IDevn3bObsfyuWhVYy2BDy4Npp0qbfH+Ii0X81vjzYTtYRBtiXIeVMmEJVAC6RC3RTdUZ3dN5C7uDyU+1k5HmyTTYhz+27c5y7tZgl5qBWM7VvJmRAITyA6gRYCCei1a9fmFrKU8fi6PBaJiG+YWHhTxV2joi/MlmP6OmkaJlfX6vIScipqa5EAAAegSURBVAQ67v7B3dsJRCnQpimL8jVo8klRHnqQ68qi/fLGXGKeaseV7eRPbivU1Q17p5pgK1X70a5hCEQt0E1cHjqmLlqgLkJE5yLO/XZEvRy1vZhxK5lNfheNnGXTcvQHmQb7tRO1j0MgeoFu6vLQiEtZ5xSSZRY1yK0hgbYVjboVFdJXjPU45k73qgh0urbNuWVJCHRTl4eO06hL+TxOnTrljAYJtetHzh1r6LYj0EMT53pDEEhKoAVMk1Fa1KJJozYFcW5DbfxzbJO87Bc5vl24g24EkhNon9F0FR3Jj7p1pjHP1jLv6kuZREljWoRrhyCQrEAb37Q+fdfW1hayGiON6cKb4oDGBBDoxqg4MCICyQt03URg1U5MDEbUcyu3ikDHazvu3E0gWYFus8GowXT9+vVZEh9KPARsKU5JNRqP/bhTO4EkBXpRzg7tEK5j6nbs0GhaMdBNM63RwcYjEGIfxPHunitDIKMRtPJpVDOelZtvFp40WS6ueGltCrBoJSIdbFwCtsVGbXYSH7cVXB0C8wSSGUErvE5Z6Kr7Bpomu5IoaSSt5Et1S47JwzHtR4cQu2nbh7trTyAJgV7k0liUiU6j6bpoD7k5JNIhck23NxVnugjY9ozUxsKKzKFAIGYC0Qv0olwaPnsX1o2mWfQwzW6ul6smCKuFCcJp2ou78iMQtUAv8je3EdW6JPO4Ovw61xBH27LYaTXoysrKEJfnGhDolUC0Aq3l3K5ERy9fvizOnTvXenJP/uytra058Ew89doXW1Vu8z+zIrQVSk6aIIEoBbpOnH1cGnX2cCXwNzt4TNCWWd6SXqR6oZYL/ucsu0KSjY5OoOvEOfTIaXt7ey66g/zQ03kObO4N3R3+5+nYiDvpRiAqgR5SnIWVFJbdOlffZ2sD4WPHju24jBYhKXadAoEUCEQj0K7RkoygJPxKrh+6INChiYat79WrV3MV4t4Iy5jaxiUQjUDbfI1C16fLwSbQod0o45o/7qvbXFCkGI3bptz9TgJRCLRtx+a+xVn1266rxSrycVLGJ0AGu/FtwB30SyAKgbZFVAwV8mbLktbnqL1fc6dVOwKdlj1pzTyBKATa5t4YSiSVp2N1dXUHOSaipvEoIdDTsAN30R+BKATaNhkkJJubm7N8CxLMvorNzcFKtb5o+9WLQPvx4uj4CEQt0Aa3/MISagl26NSgtuiRodwr8XWn4e74ypUrxdmzZ+cuyCThcDbgSv0TiEKgbSMlFxqJp3ZECSXUhNr13wl9r2CziamDRSq+NDl+ygSiEOgmyfWrkDWi1iir644oCPT0uq/rhd1XPPz0CHBHuRCIQqCNMSTU+qxVkqS6BPtl48ntoax2bYWaZPDTexRsAq15gePHj5Oze3rm4o46EIhKoMvtlG9YP1rqW7e3oDlHAi23x9OnT2c/8lvrt6soAY+iN2zbZ7VJY9rBRpxaIcDkIF0iFwLRCnTZQBpRa2S9tLQ0iN3ICz0IZudFEOhx+XP14QgkIdAGl4RaI96m7o82mFnq3YZa2HNsW1wRvRGWMbVNg0BSAi2kxk+9trbWyPXhYwbE2YdWP8e6tjhDoPvhTa3jEkhOoA1OCfWpU6dmPmotNulaFBGysbHRtRrO70jAlmJU8wmHDx9mgrAjW06fHoFkBbqMWmK9vLxcHDx4cLb7hlwg5nedSTSpqB8JMzt6T6Pz2vzPvDynYRvuIjyBLAQ6PDZqHIsAE4Rjkee6YxBAoMegzjVbE0CgW6PjxAgJINARGi3nW0agc7Z+fm1HoPOzedQtRqCjNh8370kAgfYExuHjEdDErmKgqytHCbEbzyZcuV8CCHS/fKk9IAFbXhRVj0AHhExVkyKAQE/KHNxMHQHb7jbEQNNnUiaAQKds3cTaVt3dRuKsrc+UWpYCgRQJINApWjXhNkmktUJU2QmVSrZtGtmEEdG0hAgg0AkZk6ZAAAJpEUCg07InrYEABBIigEAnZEyaAgEIpEUAgU7LnrQGAhBIiAACnZAxaQoEIJAWAQQ6LXvSGghAICECCHRCxqQpEIBAWgQQ6LTsSWsgAIGECCDQCRmTpkAAAmkRQKDTsietgQAEEiKAQCdkTJoCAQikRQCBTsuetAYCEEiIAAKdkDFpCgQgkBYBBDote9IaCEAgIQIIdELGpCkQgEBaBBDotOxJayAAgYQIINAJGZOmQAACaRFAoNOyJ62BAAQSIoBAJ2RMmgIBCKRFAIFOy560BgIQSIgAAp2QMWkKBCCQFgEEOi170hoIQCAhAgh0QsakKRCAQFoEEOi07ElrIACBhAgg0AkZk6ZAAAJpEUCg07InrYEABBIigEAnZEyaAgEIpEUAgU7LnrQGAhBIiAACnZAxaQoEIJAWAQQ6LXvSGghAICECCHRCxqQpEIBAWgQQ6LTsSWsgAIGECCDQCRmTpkAAAmkRQKDTsietgQAEEiKAQCdkTJoCAQikRQCBTsuetAYCEEiIAAKdkDFpCgQgkBYBBDote9IaCEAgIQIIdELGpCkQgEBaBBDotOxJayAAgYQIINAJGZOmQAACaRFAoNOyJ62BAAQSIoBAJ2RMmgIBCKRFAIFOy560BgIQSIjA/wAvCXsenSDWmAAAAABJRU5ErkJggg==";

        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(appContext) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        try {
                            FileOperator optr = new FileOperator(appContext);
                            optr.moveSvmModelFromAssetsDirToFilesDir(appContext);
                            HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
                            int result=handWriteRecognizer.recognizeBase64FormatImg(base64FormatStr,appContext);
                            Log.d("InstrumentTest","predict result is --->"+result );
                        } catch (Exception e) {
                            Log.e("InstrumentTest", e.getMessage());
                        }
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, appContext, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
