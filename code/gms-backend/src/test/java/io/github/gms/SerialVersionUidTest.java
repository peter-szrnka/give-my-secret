package io.github.gms;

import com.google.common.collect.Sets;
import io.github.gms.common.abstraction.AbstractGmsEntity;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.github.gms.util.TestUtils.getAllSubClasses;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class SerialVersionUidTest {

    @Test
    void serialVersionUidTest_whenAllEntitiesHaveSerialVersionUidField_thenTestPass() {
        Set<Class<?>> allSubClasses = getAllSubClasses(AbstractGmsEntity.class);
        Set<String> missingSerialVersionUid = Sets.newHashSet();

        allSubClasses.forEach(cls -> collectMissingSerialVersionUid(missingSerialVersionUid, cls));

        assertTrue("Some entities do not have serialVersionUid field: \r\n- " + String.join("\r\n- ", missingSerialVersionUid),
                missingSerialVersionUid.isEmpty());
    }

    private static void collectMissingSerialVersionUid(Set<String> missingSerialVersionUid, Class<?> cls) {
        if (cls.getDeclaredFields().length == 0) {
            return;
        }

        boolean hasSerialVersionUid = false;
        for (var field : cls.getDeclaredFields()) {
            if (field.getName().equals("serialVersionUID")) {
                hasSerialVersionUid = true;
                break;
            }
        }

        if (!hasSerialVersionUid) {
            missingSerialVersionUid.add(cls.getSimpleName());
        }
    }
}
