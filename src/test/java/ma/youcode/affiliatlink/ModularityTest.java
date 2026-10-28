
package ma.youcode.affiliatlink;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    @Test
    void verifiesModuleStructure() {
        ApplicationModules.of(AffiliatLinkApplication.class)
                .verify();
    }
}
