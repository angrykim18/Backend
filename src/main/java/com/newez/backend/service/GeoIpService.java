package com.newez.backend.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Optional;

@Service
public class GeoIpService {

    private DatabaseReader databaseReader;

    @PostConstruct
    public void init() throws IOException {
        try (InputStream dbStream = getClass().getClassLoader().getResourceAsStream("geoip/GeoLite2-Country.mmdb")) {
            if (dbStream == null) {
                System.err.println("CRITICAL ERROR: GeoIP 데이터베이스 파일을 'resources/geoip/' 폴더에서 찾을 수 없습니다.");
                return;
            }
            databaseReader = new DatabaseReader.Builder(dbStream).build();
        }
    }

    public String getCountry(String ip) {
        if (databaseReader == null) {
            return "GeoIP 서비스 미초기화";
        }
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);

            // ▼▼▼ [수정] 한국어 이름이 없을 경우, 기본(영어) 이름을 대신 사용하도록 로직 변경 ▼▼▼
            return Optional.ofNullable(databaseReader.country(ipAddress))
                    .map(CountryResponse::getCountry)
                    .map(country -> {
                        String koreanName = country.getNames().get("ko");
                        // 한국어 이름이 있으면 한국어 이름을, 없으면 기본 영어 이름을 반환
                        return (koreanName != null) ? koreanName : country.getName();
                    })
                    .orElse("알 수 없음");

        } catch (IOException | GeoIp2Exception e) {
            return "국가 정보 없음";
        }
    }
}