package org.gephi.toolkit.benchmark.layout;


import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphModel;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.layout.spi.Layout;
import org.gephi.toolkit.benchmark.GexfGraph;

import org.openjdk.jmh.annotations.*;
import snapshot.org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;


@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
public class ForceAtlas2StableVsSnapshotBenchmark {


    @State(Scope.Benchmark)
    public static class BenchmarkState  {




        @Param({"snapshot","stable"})
        public String layout;

        @Param({"comic-hero-network.gexf","rfc.gexf","World_flight_routes.gexf"})
        public String gexf;

        @Param({"true","false"})
        public boolean barnesHut;
        public Layout useLayout = null;

        @Setup(Level.Trial)
        public void setup() {

            GraphModel graphModel = GexfGraph.loadGexf(GexfGraph.getFile(gexf));

            if(layout.equals("stable"))
            {
                org.gephi.layout.plugin.forceAtlas2.ForceAtlas2 current_layout = new ForceAtlas2Builder().buildLayout();
                current_layout.setBarnesHutOptimize(barnesHut);
                useLayout = current_layout;
            }
            if(layout.equals("snapshot"))
            {
                ForceAtlas2 current_layout = new snapshot.org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder().buildLayout();
                current_layout.setBarnesHutOptimize(barnesHut);
                useLayout = current_layout;
            }
            if(layout == null){
                throw new RuntimeException("layout is null");
            }

            useLayout.setGraphModel(graphModel);
            useLayout.initAlgo();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            useLayout.endAlgo();
        }
    }

    @Benchmark
    public void goAlgo(BenchmarkState state) {
        state.useLayout.goAlgo();
    }
}
