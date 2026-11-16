package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.pathfinder.FileGraphLoader;
import software.ulpgc.pathfinder.GraphContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileGraphLoaderTest {
	@TempDir
	Path tempDir;

	@Test
	void testLoadValidGraph() throws IOException {
		File file = write("A,B,1.5", "B,C,2", "A,C,10");
		GraphContainer container = new FileGraphLoader(file).load();
		assertEquals(List.of("A", "B", "C"), container.shortestPathBetween("A", "C"));
		assertEquals(3.5, container.pathWeightBetween("A", "C"), 1e-9);
	}

	@Test
	void testInvalidLinesAreSkipped() throws IOException {
		File file = write("A,B,1", "linea sin comas", "B,C,no-es-un-numero", "B,D,4");
		GraphContainer container = new FileGraphLoader(file).load();
		assertEquals(5.0, container.pathWeightBetween("A", "D"), 1e-9);
		assertThrows(IllegalArgumentException.class, () -> container.shortestPathBetween("A", "C"));
	}

	@Test
	void testMissingFileThrowsIOException() {
		File file = tempDir.resolve("no-existe.csv").toFile();
		assertThrows(IOException.class, () -> new FileGraphLoader(file).load());
	}

	private File write(String... lines) throws IOException {
		Path path = tempDir.resolve("graph.csv");
		Files.write(path, List.of(lines));
		return path.toFile();
	}
}
