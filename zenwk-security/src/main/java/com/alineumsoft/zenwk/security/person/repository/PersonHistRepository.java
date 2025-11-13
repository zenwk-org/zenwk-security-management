package com.alineumsoft.zenwk.security.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alineumsoft.zenwk.security.person.entity.PersonHist;

/**
 * @author <a href="mailto:alineumsoft@gmail.com">C. Alegria</a>
 * @project SecurityUser
 * @class PersonHistRepository
 */
public interface PersonHistRepository extends JpaRepository<PersonHist, Long> {

}
