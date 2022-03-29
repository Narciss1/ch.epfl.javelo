package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiRouteTestLesBoss {
    @Test
    public void TestConstructeurthrow() {
        List<Route> newRoute = new ArrayList<>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MultiRoute(newRoute));
    }
    @Test
    public void testConstructeur(){
        List<Route> newRoute = new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 15, null),
                new Edge(4, 4, null, null, 15, null)
        )))));
        Assertions.assertDoesNotThrow(() -> new MultiRoute(newRoute));
    }

    @Test
    public void testPrivateVariable()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 15, null),
                new Edge(4, 4, null, null, 15, null)
        ))))));
        for (Field declaredField : mr.getClass().getDeclaredFields()) {
            Assertions.assertThrows(IllegalAccessException.class, () -> declaredField.set(mr, null));
        }
    }

    @Test
    public void testFinalVariable()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 15, null),
                new Edge(4, 4, null, null, 15, null)
        ))))));
        for (Field declaredField : mr.getClass().getDeclaredFields()) {
            Assertions.assertTrue(Modifier.isFinal(declaredField.getModifiers()));
        }
    }

    @Test
    public void testPrivateVariable2()
    {
        for (Field declaredField : RouteComputer.class.getDeclaredFields()) {
            Assertions.assertTrue(Modifier.isPrivate(declaredField.getModifiers()));
        }
    }

    @Test
    public void testFinalVariable2()
    {
        for (Field declaredField : RouteComputer.class.getDeclaredFields()) {
            Assertions.assertTrue(Modifier.isFinal(declaredField.getModifiers()));
        }
    }

    @Test
    public void lengtheaze()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 15, null),
                new Edge(4, 4, null, null, 15, null)
        ))))));
        Assertions.assertEquals(30, mr.length());
    }
    @Test
    public void lengtheaze2()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 5, null),
                new Edge(4, 4, null, null, 15, null)
        ))))));
        Assertions.assertEquals(20, mr.length());
    }
    @Test
    public void lengtheaze3()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 60, null),
                new Edge(4, 4, null, null, 9, null)
        ))))));
        Assertions.assertEquals(69, mr.length());
    }
    @Test
    public void lengtheaze4()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 0, null),
                new Edge(4, 4, null, null, 0, null)
        ))))));
        Assertions.assertEquals(0, mr.length());
    }
    @Test
    public void lengtheaze5()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                )
        )));
        Assertions.assertEquals(420, mr.length());
    }
    @Test
    public void lengtheaze10()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                )
        )));
        MultiRoute mr2 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                )
                ,
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                ), mr1

        )));
        Assertions.assertEquals(840, mr2.length());
    }
    @Test
    public void edgesSize()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                )
        )));
        MultiRoute mr2 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                )
                ,
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                ), mr1

        )));
        Assertions.assertEquals(8, mr2.edges().size());
    }
    @Test
    public void pointsSize()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                )
        )));
        MultiRoute mr2 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                )
                ,
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                ), mr1

        )));
        Assertions.assertEquals(9, mr2.points().size());
    }

    @Test
    public void pointAtTest()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(6, 0), 6, null),
                                new Edge(4, 4, pointCreator(6, 0), pointCreator(15, 0), 9, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(15, 0), pointCreator(435, 0), 420, null),
                                new Edge(4, 4, pointCreator(435, 0), pointCreator(436, 0), 1, null))
                )
                )
        )));
        for (int i = 0; i < mr1.length(); i++) {
            Assertions.assertEquals(SwissBounds.MIN_E + i, mr1.pointAt(i).e() );
        }

    }

    @Test
    public void NodeClosestTo()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(6, 0), 6, null),
                                new Edge(4, 5, pointCreator(6, 0), pointCreator(15, 0), 9, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(5, 6, pointCreator(15, 0), pointCreator(435, 0), 420, null),
                                new Edge(6, 7, pointCreator(435, 0), pointCreator(436, 0), 1, null))
                )
                )
        )));
        Assertions.assertEquals(3, mr1.nodeClosestTo(2));
        Assertions.assertEquals(4, mr1.nodeClosestTo(5));
        Assertions.assertEquals(5, mr1.nodeClosestTo(14));
    }

    @Test
    public void pointClosestTo()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(10, 0), 10, null),
                                new Edge(4, 5, pointCreator(10, 0), pointCreator(20, 0), 10, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(5, 6, pointCreator(20, 0), pointCreator(30, 0), 10, null),
                                new Edge(6, 7, pointCreator(30, 0), pointCreator(40, 0), 10, null))
                )
                )
        )));
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(i + SwissBounds.MIN_E, mr1.pointClosestTo(pointCreator(i, 1)).point().e());
        }
    }

    @Test
    public void ElevationAt()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(6, 0), 6, d -> 5),
                                new Edge(4, 5, pointCreator(6, 0), pointCreator(15, 0), 9, d -> 0))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(5, 6, pointCreator(15, 0), pointCreator(435, 0), 420, Functions.sampled(new float[]{1,1,1}, 2)),
                                new Edge(6, 7, pointCreator(435, 0), pointCreator(436, 0), 1, d -> 10))
                )
                )
        )));
        Assertions.assertEquals(5, mr1.elevationAt(1));
        Assertions.assertEquals(5, mr1.elevationAt(2));
        Assertions.assertEquals(5, mr1.elevationAt(3));
        Assertions.assertEquals(5, mr1.elevationAt(5));
        Assertions.assertEquals(0, mr1.elevationAt(6));
        Assertions.assertEquals(0, mr1.elevationAt(7));
        Assertions.assertEquals(0, mr1.elevationAt(8));
        Assertions.assertEquals(0, mr1.elevationAt(9));
        Assertions.assertEquals(1, mr1.elevationAt(16));

        Assertions.assertEquals(10, mr1.elevationAt(436));
    }


    private PointCh pointCreator(int e, int n)
    {
        return new PointCh(SwissBounds.MIN_E + e, SwissBounds.MIN_N + n);
    }
}
