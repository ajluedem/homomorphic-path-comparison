import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import security.dgk.DGKKeyPairGenerator;
import security.misc.HomomorphicException;
import security.paillier.PaillierCipher;
import security.paillier.PaillierKeyPairGenerator;
import security.paillier.PaillierPublicKey;
import security.socialistmillionaire.alice_joye;
import security.socialistmillionaire.bob_joye;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.Socket;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

public class IntersectTest {
    private static final Logger logger = LogManager.getLogger(IntersectTest.class);

    private static KeyPair dgk = null;
    private static KeyPair paillier = null;
    @Before
    public void read_properties() {

    }

    @Before
    public void generate_keys() {
        DGKKeyPairGenerator p = new DGKKeyPairGenerator();
        p.initialize(2048,null);
        dgk = p.generateKeyPair();

        PaillierKeyPairGenerator pa = new PaillierKeyPairGenerator();
        pa.initialize(2048,null);
        paillier = pa.generateKeyPair();
    }

    @Test
    public void encrypted_test_intersections() {
        String answers_path = new File("data/testroutine.csv").toString();
        // Parse CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(answers_path))) {
            String line;
            alice_joye alice = new alice_joye();
            bob_joye bob = new bob_joye(paillier, dgk);

            while ((line = br.readLine()) != null) {
                // Set-up
                Thread multiply = new Thread(new BobThread(bob,9200));
                multiply.start();

                alice.set_socket(connectWithRetry("127.0.0.1", 9200));
                alice.receivePublicKeys();

                PaillierPublicKey paillier_public_key = (PaillierPublicKey) paillier.getPublic();
                EncryptedPathsComparison testing = new EncryptedPathsComparison(alice);

                // Parse data and run-test
                String[] values = line.split(",");
                String ownroute = values[0];
                String cryptroute = values[1];
                String expected_result = values[2];
                System.out.println("Reading the file: " + ownroute);
                boolean will_collide = Boolean.parseBoolean(expected_result);

                // Parsing routes
                List<BigIntPoint> ownroute_list = shared.read_all_paths(ownroute);
                List<BigIntPoint> cryptroute_list = shared.read_all_paths(cryptroute);

                // Encrypt routes
                List<BigIntPoint> encryptedownroute_list = shared.encrypt_paillier(ownroute_list, paillier_public_key);
                List<BigIntPoint> encryptedcryptroute_list = shared.encrypt_paillier(cryptroute_list, paillier_public_key);

                // Update testing...
                List<Integer> indexes = testing.encryptedWhereIntersection(encryptedownroute_list, encryptedcryptroute_list,
                        paillier_public_key, paillier_public_key.ZERO());

                System.out.println("Indexes");
                for (Integer i: indexes) {
                    System.out.println(i);
                }

                // Bob Thread should be complete, so clean up the Thread
                // This makes sure that port 9200 will be ready when we restart the for loop
                multiply.join();

                // If the index is empty, no collision
                assertEquals(!indexes.isEmpty(), will_collide);
            }
        }
        catch (IOException | ClassNotFoundException | HomomorphicException | InterruptedException e) {
            logger.fatal(e);
        }
    }

    public static Socket connectWithRetry(String host, int port) {
        while (true) {
            try {
                // Attempt to connect
                Socket socket = new Socket(host, port);

                // If the connection is successful, print a message and break out of the loop
                System.out.println("Connected successfully!");
                return socket;
            }
            catch (ConnectException e) {
                // Connection refused, print a message and retry after a delay
                System.out.println("Connection refused. Retrying in 1 second...");
                try {
                    Thread.sleep(1000); // Wait for 1 second before retrying
                }
                catch (InterruptedException ex) {
                    logger.info(ex);
                }
            }
            catch (Exception e) {
                logger.fatal(e);
            }
        }
    }

    @Test
    public void test_intersections() throws Exception {
        String answers_path = new File("data/testroutine.csv").toString();
        //Parse CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(answers_path))){
            String assertstring;
            String line;
            while ((line = br.readLine()) != null){
                String [] values = line.split(",");
                String ownroute = values[0];
                String cryptroute = values[1];
                String expected_result = values[2];
                System.out.println(ownroute);
                // Parsing routes
                List<BigIntPoint> ownroute_list = shared.read_all_paths(ownroute);
                List<BigIntPoint> cryptroute_list = shared.read_all_paths(cryptroute);
                // Testing for intersection
                boolean output = CleartextPathsComparison.pathIntersection(ownroute_list, cryptroute_list);
                if (output) {
                    assertstring = "true";
                }
                else {
                     assertstring = "false";
                }
                assertEquals(expected_result, assertstring);
            }
        }
    }
}