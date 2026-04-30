package com.hris.config;

import com.hris.entity.*;
import com.hris.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final JabatanRepository jabatanRepository;
    private final DepartemenRepository departemenRepository;
    private final UnitKerjaRepository unitKerjaRepository;
    private final PendidikanRepository pendidikanRepository;
    private final JenisKelaminRepository jenisKelaminRepository;
    private final StatusAbsenRepository statusAbsenRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedJabatan();
        seedDepartemen();
        seedUnitKerja();
        seedPendidikan();
        seedJenisKelamin();
        seedStatusAbsen();
    }

    private void seedJabatan() {
        if (jabatanRepository.count() == 0) {
            jabatanRepository.saveAll(List.of(
                new Jabatan(null, "Direktur"),
                new Jabatan(null, "Manajer"),
                new Jabatan(null, "Supervisor"),
                new Jabatan(null, "Staff"),
                new Jabatan(null, "Admin"),
                new Jabatan(null, "HRD Manager"),
                new Jabatan(null, "HRD Staff")
            ));
        }
    }

    private void seedDepartemen() {
        if (departemenRepository.count() == 0) {
            departemenRepository.saveAll(List.of(
                new Departemen(null, "HRD"),
                new Departemen(null, "Keuangan"),
                new Departemen(null, "IT"),
                new Departemen(null, "Operasional"),
                new Departemen(null, "Marketing"),
                new Departemen(null, "Legal")
            ));
        }
    }

    private void seedUnitKerja() {
        if (unitKerjaRepository.count() == 0) {
            unitKerjaRepository.saveAll(List.of(
                new UnitKerja(null, "Kantor Pusat"),
                new UnitKerja(null, "Cabang Jakarta"),
                new UnitKerja(null, "Cabang Bandung"),
                new UnitKerja(null, "Cabang Surabaya"),
                new UnitKerja(null, "Cabang Medan")
            ));
        }
    }

    private void seedPendidikan() {
        if (pendidikanRepository.count() == 0) {
            pendidikanRepository.saveAll(List.of(
                new Pendidikan(null, "SD"),
                new Pendidikan(null, "SMP"),
                new Pendidikan(null, "SMA/SMK"),
                new Pendidikan(null, "D3"),
                new Pendidikan(null, "S1"),
                new Pendidikan(null, "S2"),
                new Pendidikan(null, "S3")
            ));
        }
    }

    private void seedJenisKelamin() {
        if (jenisKelaminRepository.count() == 0) {
            jenisKelaminRepository.saveAll(List.of(
                new JenisKelamin(null, "Laki-Laki"),
                new JenisKelamin(null, "Perempuan")
            ));
        }
    }

    private void seedStatusAbsen() {
        if (statusAbsenRepository.count() == 0) {
            statusAbsenRepository.saveAll(List.of(
                new StatusAbsen(null, "Hadir"),
                new StatusAbsen(null, "Sakit"),
                new StatusAbsen(null, "Izin"),
                new StatusAbsen(null, "Cuti"),
                new StatusAbsen(null, "Alpha")
            ));
        }
    }
}
