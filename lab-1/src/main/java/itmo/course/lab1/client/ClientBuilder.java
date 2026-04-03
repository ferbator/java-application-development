package itmo.course.lab1.client;

public final class ClientBuilder {
    private ClientBuilder() {
    }

    public static FirstNameStep builder() {
        return new Steps();
    }

    public interface FirstNameStep {
        LastNameStep firstName(String firstName);
    }

    public interface LastNameStep {
        OptionalDataStep lastName(String lastName);
    }

    public interface OptionalDataStep {
        OptionalDataStep address(String address);

        OptionalDataStep passportNumber(String passportNumber);

        Client build();
    }

    private static class Steps implements FirstNameStep, LastNameStep, OptionalDataStep {
        private String firstName;
        private String lastName;
        private String address;
        private String passportNumber;

        @Override
        public LastNameStep firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        @Override
        public OptionalDataStep lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        @Override
        public OptionalDataStep address(String address) {
            this.address = address;
            return this;
        }

        @Override
        public OptionalDataStep passportNumber(String passportNumber) {
            this.passportNumber = passportNumber;
            return this;
        }

        @Override
        public Client build() {
            return new Client(firstName, lastName, address, passportNumber);
        }
    }
}
