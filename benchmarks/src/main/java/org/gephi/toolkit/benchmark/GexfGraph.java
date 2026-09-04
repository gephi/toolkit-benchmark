package org.gephi.toolkit.benchmark;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;

import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.Workspace;
import org.gephi.toolkit.benchmark.layout.ForceAtlas2StableVsSnapshotBenchmark;
import org.openide.util.Lookup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class GexfGraph {

    private GexfGraph() {}
    static public  File getFile(String filename) {

        String classpathResource = "/org/gephi/toolkit/benchmark/samples/" + filename;
        try (InputStream in = ForceAtlas2StableVsSnapshotBenchmark.class.getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Sample resource not found on classpath: " + classpathResource);
            }
            final String suffix = classpathResource.substring(classpathResource.lastIndexOf('.'));
            final Path tempFile = Files.createTempFile("viz-engine-demo-", suffix);
            tempFile.toFile().deleteOnExit();
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile.toFile();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to extract sample: " + classpathResource, e);
        }
    }
    static public GraphModel loadGexf(File file){

        Container container =   Lookup.getDefault().lookup(Container.Factory.class).newContainer();
        final ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        try {
            container = importController.importFile(file);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to import graph file: " + file.getAbsolutePath(), e);
        }
        Workspace workspace = Lookup.getDefault().lookup(ImportController.class)
                .process(container, new DefaultProcessor(), null);

        return Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
    }
}
