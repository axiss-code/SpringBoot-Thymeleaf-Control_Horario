package com.ilerna.wr;

import com.ilerna.wr.entity.Area;
import com.ilerna.wr.entity.User;
import com.ilerna.wr.repository.IAreaRepository;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class JunitTests {
    
    @Autowired
    private IAreaRepository ar;
    
    @Test
    public void testExisteArea() {
        Long areaId=1L;
        Optional<Area> existeArea = ar.findById(areaId);
        assertThat(existeArea).isNotEmpty();
    }
    
    @Test
    public void testExisteUser() {
        Long userId=1L;
        Optional<User> existeUser = ar.verify(userId);
        assertThat(existeUser).isNotEmpty();
    }
    
    @Test
    public void testAsociarArea() {
        Long userId=1L;
        Long areaId=1L;
        Area area = ar.findById(areaId).get();
        User user = ar.verify(userId).get();
        area.addUser(user);
        assertThat(ar.save(area)).isNotNull();
    }
    
}
