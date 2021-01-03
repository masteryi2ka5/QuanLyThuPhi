package controller;

import model.KhoanPhi;
import model.ThuPhiHoGiaDinh;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class QuanLyThuPhiHoGiaDinh {
    public DBConnection dbConnection = new DBConnection();
    public Connection cnn;

    private QuanLyHoGiaDinh quanLyHoGiaDinh = new QuanLyHoGiaDinh();

    public QuanLyThuPhiHoGiaDinh() {
        this.cnn = this.dbConnection.getConnection();
    }

    public ArrayList<ThuPhiHoGiaDinh> getThuthuPhiHoGiaDinhList() throws SQLException {
        return selectThuPhiHoGiaDinh();
    }

    public ArrayList<ThuPhiHoGiaDinh> selectThuPhiHoGiaDinh() throws SQLException {
        Statement stm = this.cnn.createStatement();
        String selQuery = "SELECT * FROM Thu_phi_ho_gia_dinh";
        ResultSet selSet = stm.executeQuery(selQuery);
        ArrayList<ThuPhiHoGiaDinh> selThuPhiHoGiaDinhList = new ArrayList<>();
        while (selSet.next()) {
            String maHoGiaDinh1 = selSet.getString("Ma_ho");
            String maPhi1 = selSet.getString("Ma_Phi");
            Integer soTienDaNop1 = selSet.getInt("So_tien_da_nop");
            Integer soTienConThieu1 = -1;
            LocalDate ngayNop = selSet.getDate("Ngay_nop").toLocalDate();

            String loaiPhi = (String) getKhoanPhiInfor(maPhi1).get("Loai_phi");
//            System.out.println(loaiPhi);
            if (loaiPhi.equals("TP01")) {
                soTienConThieu1 = Math.max(0, (int) getKhoanPhiInfor(maPhi1).get("So_tien_can_thu") - soTienDaNop1);
//            	System.out.println(soTienConThieu1);
            } else if (loaiPhi.equals("TP02")) {
//    			System.out.println(maPhi1);
//    			System.out.println(quanLyHoGiaDinh.getHoGiaDinh(maPhi1).getSoNhanKhau());
                soTienConThieu1 = Math.max(0, (int) getKhoanPhiInfor(maPhi1).get("So_tien_can_thu")
                        * quanLyHoGiaDinh.getHoGiaDinh(maHoGiaDinh1).getSoNhanKhau() - soTienDaNop1);
            } else if (loaiPhi.equals("DG00")) {
                soTienConThieu1 = 0;
            }

            ThuPhiHoGiaDinh b = new ThuPhiHoGiaDinh(maHoGiaDinh1, maPhi1, soTienDaNop1, soTienConThieu1, ngayNop);
            selThuPhiHoGiaDinhList.add(b);
        }
        return selThuPhiHoGiaDinhList;
    }

    public boolean addThuPhiHoGiaDinh(ThuPhiHoGiaDinh tmp) {
        try {
            Statement stm = this.cnn.createStatement();
            String selQuery = "insert into Thu_phi_ho_gia_dinh values('" + tmp.getMaHoGiaDinh() + "', '"
                    + tmp.getMaPhi() + "', " + tmp.getSoTienDaNop() + ", '" + tmp.getNgayNop() + "')";
            stm.execute(selQuery);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public boolean deleteThuPhiHoGiaDinh(ThuPhiHoGiaDinh tmp) {
        try {
            Statement stm = this.cnn.createStatement();
            String selQuery = "delete from Thu_phi_ho_gia_dinh where Ma_ho='" + tmp.getMaHoGiaDinh() + "' and Ma_Phi='"
                    + tmp.getMaPhi() + "'";
            stm.execute(selQuery);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public boolean updateThuPhiHoGiaDinh(ThuPhiHoGiaDinh tmp) {
        try {
            Statement stm = this.cnn.createStatement();
            String selQuery = "update Thu_phi_ho_gia_dinh set Ma_ho='" + tmp.getMaHoGiaDinh() + "', Ma_Phi=N'"
                    + tmp.getMaPhi() + "', So_tien_da_nop=" + tmp.getSoTienDaNop() + ", Ngay_nop='" + tmp.getNgayNop()
                    + "' where Ma_ho='" + tmp.getMaHoGiaDinh() + "' and Ma_phi='" + tmp.getMaPhi() + "'";
            stm.execute(selQuery);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public ArrayList<ThuPhiHoGiaDinh> showByKhoanPhi(String maPhi, String status) {
        ArrayList<ThuPhiHoGiaDinh> listToShow = new ArrayList<>();
        Statement stm = null;
        String selQuery = "";
        String loaiPhi = (String)getKhoanPhiInfor(maPhi).get("Loai_phi");
        if (loaiPhi.equals("TP01")) {
            if (status.equals("DaNop")) {
                selQuery = " select Thu_phi_ho_gia_dinh.Ma_ho, Thu_phi_ho_gia_dinh.Ma_Phi, So_tien_da_nop, " +
                        " Ngay_nop from ho_gia_dinh, Thu_phi_ho_gia_dinh, KhoanPhi where " +
                        " Thu_phi_ho_gia_dinh.Ma_ho= ho_gia_ dinh.Ma_ho and Thu_phi_ho_gia_dinh.Ma_Phi= KhoanPhi.Ma_Phi" +
                        " and So_tien_da_nop= KhoanPhi.So_tien_can_thu and KhoanPhi.Loai_Phi='TP01' and " +
                        " Thu_phi_ho_gia_dinh.Ma_Phi=" + "'" + maPhi + "'";
            }
            else if (status.equals("ChuaNop")) {
                selQuery = " select Thu_phi_ho_gia_dinh.Ma_ho, Thu_phi_ho_gia_dinh.Ma_Phi, So_tien_da_nop, " +
                        " Ngay_nop from ho_gia_dinh, Thu_phi_ho_gia_dinh, KhoanPhi" +
                        " where Thu_phi_ho_gia_dinh.Ma_ho= ho_gia_dinh.Ma_ho and Thu_phi_ho_gia_dinh.Ma_Phi= KhoanPhi.Ma_Phi" +
                        " and So_tien_da_nop< KhoanPhi.So_tien_can_thu and KhoanPhi.Loai_Phi='TP01' and " +
                        " Thu_phi_ho_gia_dinh.Ma_Phi=' " + maPhi + "'";
            }
        }
        else if (loaiPhi.equals("TP02")) {
            if (status.equals("DaNop")) {
                selQuery = " select Thu_phi_ho_gia_dinh.Ma_ho, Thu_phi_ho_gia_dinh.Ma_Phi, So_tien_da_nop, " +
                        " Ngay_nop from ho_gia_dinh, Thu_phi_ho_gia_dinh, KhoanPhi" +
                        " where Thu_phi_ho_gia_dinh.Ma_ho= ho_gia_dinh.Ma_ho and " +
                        " Thu_phi_ho_gia_dinh.Ma_Phi= KhoanPhi.Ma_Phi" +
                        " and So_tien_da_nop= KhoanPhi.So_tien_can_thu*ho_gia_dinh.So_nhan_khau and KhoanPhi.Loai_Phi='TP02'" +
                        " and Thu_phi_ho_gia_dinh.Ma_Phi='" + maPhi + "'";
            }
            else if (status.equals("ChuaNop")) {
                selQuery = " select Thu_phi_ho_gia_dinh.Ma_ho, Thu_phi_ho_gia_dinh.Ma_Phi, So_tien_da_nop, " +
                        " Ngay_nop from ho_gia_dinh, Thu_phi_ho_gia_dinh, KhoanPhi" +
                        " where Thu_phi_ho_gia_dinh.Ma_ho= ho_gia_dinh.Ma_ho and " +
                        " Thu_phi_ho_gia_dinh.Ma_Phi= KhoanPhi.Ma_Phi" +
                        " and So_tien_da_nop< KhoanPhi.So_tien_can_thu*ho_gia_dinh.So_nhan_khau and KhoanPhi.Loai_Phi='TP02'" +
                        " and Thu_phi_ho_gia_dinh.Ma_Phi='" + maPhi + "'";
            }
        }
        else if (loaiPhi.equals("DG00")) {
            selQuery = " select Thu_phi_ho_gia_dinh.Ma_ho, Thu_phi_ho_gia_dinh.Ma_Phi, So_tien_da_nop, Ngay_nop from ho_gia_dinh, " +
                    " Thu_phi_ho_gia_dinh, KhoanPhi" +
                    " where Thu_phi_ho_gia_dinh.Ma_ho= ho_gia_dinh.Ma_ho and Thu_phi_ho_gia_dinh.Ma_Phi= KhoanPhi.Ma_Phi" +
                    " and KhoanPhi.Loai_Phi='DG00'" + " and Thu_phi_ho_gia_dinh.Ma_Phi='" + maPhi + "'";;
        }
        try {
            stm = this.cnn.createStatement();
            ResultSet resultSet = stm.executeQuery(selQuery);
            if (status.equals("ChuaNop")) {
                if (loaiPhi.equals("TP01")) {
                    ResultSet resultSet1 = stm.executeQuery("select ho_gia_dinh.Ma_ho from ho_gia_dinh" +
                            " where ho_gia_dinh.Ma_ho not in (select distinct Ma_ho from Thu_phi_ho_gia_dinh where " +
                            " Thu_phi_ho_gia_dinh.Ma_Phi='" + maPhi + "')");
                    while (resultSet1.next()) {
                        String maHoGiaDinh = resultSet.getString("Ma_ho");
                        Integer soTienDaNop = 0;
                        Integer soTienConThieu = (int)getKhoanPhiInfor(maPhi).get("So_tien_can_thu");
                        LocalDate ngayNop = LocalDate.now();
                        ThuPhiHoGiaDinh tmp = new ThuPhiHoGiaDinh(maHoGiaDinh, maPhi, soTienDaNop, soTienConThieu, ngayNop);
                        listToShow.add(tmp);
                    }
                    while (resultSet.next()) {
                        String maHoGiaDinh = resultSet.getString("Ma_ho");
                        Integer soTienDaNop = resultSet.getInt("So_tien_da_nop");
                        Integer soTienConThieu = Math.max(0, (int) getKhoanPhiInfor(maPhi).get("So_tien_can_thu")- soTienDaNop);
                        LocalDate ngayNop = LocalDate.now();
                        ThuPhiHoGiaDinh tmp = new ThuPhiHoGiaDinh(maHoGiaDinh, maPhi, soTienDaNop, soTienConThieu, ngayNop);
                        listToShow.add(tmp);
                    }

                }
                else if (loaiPhi.equals("TP02")) {
                    ResultSet resultSet1 = stm.executeQuery("select ho_gia_dinh.Ma_ho from ho_gia_dinh " +
                            " where ho_gia_dinh.Ma_ho not in (select distinct Ma_ho from Thu_phi_ho_gia_dinh where " +
                            " Thu_phi_ho_gia_dinh.Ma_Phi='" + maPhi + "')");
                    while (resultSet1.next()) {
                        String maHoGiaDinh = resultSet.getString("Ma_ho");
                        Integer soTienDaNop = 0;
                        Integer soTienConThieu = (int)getKhoanPhiInfor(maPhi).get("So_tien_can_thu")
                                * quanLyHoGiaDinh.getHoGiaDinh(maHoGiaDinh).getSoNhanKhau();
                        LocalDate ngayNop = LocalDate.now();
                        ThuPhiHoGiaDinh tmp = new ThuPhiHoGiaDinh(maHoGiaDinh, maPhi, soTienDaNop, soTienConThieu, ngayNop);
                        listToShow.add(tmp);
                    }
                    while (resultSet.next()) {
                        String maHoGiaDinh = resultSet.getString("Ma_ho");
                        Integer soTienDaNop = resultSet.getInt("So_tien_da_nop");
                        Integer soTienConThieu = Math.max(0, (int) getKhoanPhiInfor(maPhi).get("So_tien_can_thu")
                                * quanLyHoGiaDinh.getHoGiaDinh(maHoGiaDinh).getSoNhanKhau() - soTienDaNop);
                        LocalDate ngayNop = LocalDate.now();
                        ThuPhiHoGiaDinh tmp = new ThuPhiHoGiaDinh(maHoGiaDinh, maPhi, soTienDaNop, soTienConThieu, ngayNop);
                        listToShow.add(tmp);
                    }
                }
            }
            else if (status.equals("DaNop")) {
                while (resultSet.next()) {
                    String maHoGiaDinh = resultSet.getString("Ma_ho");
                    Integer soTienDaNop = resultSet.getInt("So_tien_da_nop");
                    Integer soTienConThieu = 0;
                    LocalDate ngayNop = LocalDate.now();
                    ThuPhiHoGiaDinh tmp = new ThuPhiHoGiaDinh(maHoGiaDinh, maPhi, soTienDaNop, soTienConThieu, ngayNop);
                    listToShow.add(tmp);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listToShow;
    }

    public HashMap<String, Object> getKhoanPhiInfor(String Ma_phi) {
        HashMap<String, Object> khoanPhiInfor = new HashMap<String, Object>();
        try {
            Statement stm = this.cnn.createStatement();
            String selQuery = "select * from KhoanPhi where Ma_phi='" + Ma_phi + "'";
            ResultSet rs = stm.executeQuery(selQuery);
            while (rs.next()) {
                khoanPhiInfor.put("Loai_phi", rs.getString("Loai_phi"));
                khoanPhiInfor.put("Ma_phi", rs.getString("Ma_phi"));
                khoanPhiInfor.put("So_tien_can_thu", rs.getInt("So_tien_can_thu"));
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return khoanPhiInfor;
    }

}
