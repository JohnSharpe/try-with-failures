import java.io.IOException;

public class Main {

    public static void main(String[] args) {
            implicitChain();
            System.out.println("========");
            explicitChain();
            System.out.println("========");
            errorInImplicitChain();
            System.out.println("========");
            errorInExplicitChain();
    }

    private static void implicitChain() {
        try (final var topResource = new WellBehavedResource("top", new WellBehavedResource("middle", new WellBehavedResource("terminal")))) {
            System.out.println("Some work");
        }
    }

    private static void explicitChain() {
        try (final var terminalResource = new WellBehavedResource("terminal");
             final var middleResource = new WellBehavedResource("middle", terminalResource);
             final var topResource = new WellBehavedResource("top", middleResource)) {
            System.out.println("Some work");
        }
    }

    private static void errorInImplicitChain() {
        try (final var topResource = new WellBehavedResource("top", new TroublesomeResource("middle", new WellBehavedResource("terminal")))) {
            System.out.println("Some work");
        } catch (IOException e) {
            System.out.println("Caught the IOException!");
        }
    }

    private static void errorInExplicitChain() {
        try (final var terminalResource = new WellBehavedResource("terminal");
             final var middleResource = new TroublesomeResource("middle", terminalResource);
             final var topResource = new WellBehavedResource("top", middleResource)) {
            System.out.println("Some work");
        } catch (IOException e) {
            System.out.println("Caught the IOException!");
        }
    }

    private static abstract class Resource implements AutoCloseable {

        private final String label;
        private final Resource resource;

        public Resource(final String label) {
            this.label = label;
            System.out.println("Opening " + this.label);
            this.resource = null;
        }

        public Resource(final String label, final Resource resource) {
            this.label = label;
            System.out.println("Opening " + this.label + " with child");
            this.resource = resource;
        }

        @Override
        public void close() {
            if (this.resource == null) {
                System.out.println("Closing " + this.label + ", end of chain");
            } else {
                System.out.println("Closing " + this.label + ", onto child");
                this.resource.close();
            }
        }
    }

    private static class WellBehavedResource extends Resource {

        public WellBehavedResource(String label) {
            super(label);
        }

        public WellBehavedResource(String label, Resource resource) {
            super(label, resource);
        }

    }

    private static class TroublesomeResource extends Resource {

        public TroublesomeResource(String label) throws IOException {
            super(label);
            throw new IOException();
        }

        public TroublesomeResource(String label, Resource resource) throws IOException {
            super(label, resource);
            throw new IOException();
        }
    }

}
