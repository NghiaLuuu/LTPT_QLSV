package iuh.fit.se.server.util;

import iuh.fit.se.common.model.Gender;
import iuh.fit.se.common.model.SinhVien;
import iuh.fit.se.common.dto.SinhVienDTO;

public class SinhVienMapper {

    public static SinhVienDTO toDTO(SinhVien e) {
        if (e == null) return null;
        SinhVienDTO dto = new SinhVienDTO();
        dto.setMaSV(e.getMaSV());
        dto.setHoTen(e.getHoTen());
        dto.setNgaySinh(e.getNgaySinh());
        dto.setGioiTinh(e.getGioiTinh());
        dto.setChuyenNganh(e.getChuyenNganh());
        // Lấy mã lớp từ relationship LopHoc
        dto.setLop(e.getLopHoc() != null ? e.getLopHoc().getMaLop() : null);
        dto.setDiemTB(e.getDiemTB());
        return dto;
    }

    public static SinhVien toEntity(SinhVienDTO dto) {
        if (dto == null) return null;
        SinhVien e = new SinhVien();
        e.setMaSV(dto.getMaSV());
        e.setHoTen(dto.getHoTen());
        e.setNgaySinh(dto.getNgaySinh());
        e.setGioiTinh(dto.getGioiTinh() != null ? dto.getGioiTinh() : Gender.OTHER);
        e.setChuyenNganh(dto.getChuyenNganh());
        // Note: LopHoc relationship sẽ được set trong Service layer
        // vì cần load LopHoc entity từ database
        e.setDiemTB(dto.getDiemTB());
        return e;
    }
}
