package org.example;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;


public class Program {
    private static final Map<String, String> plan_names = new HashMap<>();
    static {
            plan_names.put("Экза мен", "cf_exam");
            plan_names.put("Зачет", "cf_midterm");
            plan_names.put("Зачет с оц.", "cf_midtermwithmark");
            plan_names.put("КП", "cf_kp");
            plan_names.put("КР", "cf_coursework");
            plan_names.put("Контр.", "cf_controlwork");
    }

    static Titular parseTitular(XSSFWorkbook wb) {
        XSSFSheet sheet = Objects.requireNonNull(wb).getSheet("Титул");
        return new Titular(sheet.getRow(29).getCell(3).getStringCellValue(),
                Long.parseLong(sheet.getRow(39).getCell(22).getStringCellValue(), 10),
                sheet.getRow(41).getCell(22).getStringCellValue(),
                sheet.getRow(24).getCell(7).getStringCellValue()
        );
    }

    static List<Competention> parseCompetention(XSSFWorkbook wb) {
        XSSFSheet sheet = Objects.requireNonNull(wb).getSheet("Компетенции");
        int i = 1;
        String subIndex;
        String index;
        String description;
        String type;
        int f = 1;
        while (f < 10) {
            if (sheet.getRow(f).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() == CellType.BLANK ||
                    sheet.getRow(f).getCell(0).getStringCellValue().equals(" ")) {
                f++;
            } else {
                f = 0;
                break;
            }
        }
        if (f > 0) {
            return parseCompetentionLegacy(sheet);
        }
        String[] ixsList = {"", "", ""};
        List<Competention> comps = new ArrayList<>();
        while (i < sheet.getLastRowNum() + 1) {
            XSSFRow row = sheet.getRow(i);
            if (!Objects.equals(row.getCell(3).getStringCellValue(), "")) {
                subIndex = ixsList[2];
                index = row.getCell(3).getStringCellValue();
                if (Objects.equals(row.getCell(3).getStringCellValue(), "")) {
                    subIndex = ixsList[1];
                    index = row.getCell(2).getStringCellValue();
                    ixsList[2] = row.getCell(2).getStringCellValue();
                    if (Objects.equals(row.getCell(2).getStringCellValue(), "")) {
                        subIndex = ixsList[0];
                        index = row.getCell(1).getStringCellValue();
                        ixsList[1] = row.getCell(1).getStringCellValue();
                        ixsList[0] = "";
                        if (Objects.equals(row.getCell(1).getStringCellValue(), "")) {
                            subIndex = "";
                            index = row.getCell(0).getStringCellValue();
                            ixsList[0] = row.getCell(0).getStringCellValue();
                            ixsList[1] = "";
                            ixsList[2] = "";
                        }
                    }
                }
                description = row.getCell(4).getStringCellValue();
                type = row.getCell(5).getStringCellValue();
                comps.add(new Competention(subIndex, index, description, type));
                System.out.println(subIndex + " " + index + " " + description + " " + type);
            }
            i++;
        }
        return comps;
    }

    private static List<Competention> parseCompetentionLegacy(XSSFSheet sheet) {
        int i = 1;
        String subIndex;
        String index;
        String description;
        String type;
        String indexPlaceholder = "";
        List<Competention> comps = new ArrayList<>();
        while (i < sheet.getLastRowNum() + 1) {

            XSSFRow row = sheet.getRow(i);
            if (!Objects.equals(row.getCell(3).getStringCellValue(), "")) {

                if (row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() == CellType.BLANK || Objects.equals(row.getCell(2).getStringCellValue(), "")) {
                    index = row.getCell(1).getStringCellValue();
                    subIndex = " ";
                    indexPlaceholder = index;
                } else {
                    index = (row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() == CellType.BLANK) ? "-" : row.getCell(2).getStringCellValue();
                    subIndex = indexPlaceholder;
                }
                description = row.getCell(3).getStringCellValue();
                type = (row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() == CellType.BLANK) ? "-" : row.getCell(4).getStringCellValue();
                comps.add(new Competention(subIndex, index, description, type));
            }
            i++;
        }
        return comps;
    }

    static List<Plan> parsePlan(XSSFWorkbook wb, String bookType) {
        Map<String, String> plan_cols = new HashMap<>();
        boolean countInPlan;
        String index;
        String name;
        String cf_exam;
        String cf_midterm;
        String cf_kp;
        String cf_coursework;
        String cf_controlwork;
        String cf_midtermwithmark;
        String ze_expert;
        String ze_factual;
        String ah_expert;
        String ah_plan;
        String ah_controlwork;
        String ah_aud;
        String ah_sr;
        String ah_control;
        String ah_preparation;

        String faculty_code;
        String faculty_name;
        List<Plan> plans = new ArrayList<>();
        int k = 3;
        int semester_amount = 0;
        XSSFSheet sheet = Objects.requireNonNull(wb).getSheet("ПланСвод");

        if (bookType.contains("бакалавриата")) {
            semester_amount = 8;
        } else if (bookType.contains("магистратуры")) {
            semester_amount = 4;
        } else if (bookType.contains("специалитета")) {
            semester_amount = 12;
        }

        while (!Objects.equals(sheet.getRow(0).getCell(k).getStringCellValue(), "з.е.")) {
            if (plan_names.containsKey(sheet.getRow(2).getCell(k).getStringCellValue())) {
                plan_cols.put(sheet.getRow(2).getCell(k).getStringCellValue(), String.valueOf(k));

            }
            k++;
        }
        int ze_position = k;


        int j = 5;
        while (j < sheet.getLastRowNum() + 1) {
            XSSFRow row = sheet.getRow(j);
            List<String> courses_semesters = new ArrayList<>();
            if (Objects.equals(row.getCell(0).getStringCellValue(), "+") || Objects.equals(row.getCell(0).getStringCellValue(), "-")) {
                index = row.getCell(1).getStringCellValue();
                name = row.getCell(2).getStringCellValue();
                cf_exam = plan_cols.containsKey("Экза мен") ? row.getCell(Integer.parseInt(plan_cols.get("Экза мен"))).getStringCellValue() :
                        "-";
                cf_midterm = plan_cols.containsKey("Зачет") ? row.getCell(Integer.parseInt(plan_cols.get("Зачет"))).getStringCellValue() :
                        "-";
                cf_coursework = plan_cols.containsKey("КР") ? row.getCell(Integer.parseInt(plan_cols.get("КР"))).getStringCellValue() :
                        "-";
                cf_controlwork = plan_cols.containsKey("Контр.") ? row.getCell(Integer.parseInt(plan_cols.get("Контр."))).getStringCellValue() :
                        "-";
                cf_kp = plan_cols.containsKey("КП") ? row.getCell(Integer.parseInt(plan_cols.get("КП"))).getStringCellValue() :
                        "-";
                cf_midtermwithmark = plan_cols.containsKey("Зачет с оц.") ? row.getCell(Integer.parseInt(plan_cols.get("Зачет с оц."))).getStringCellValue() :
                        "-";

                ze_expert = row.getCell(ze_position).getStringCellValue();
                ze_factual = row.getCell(ze_position + 1).getStringCellValue();
                ah_expert = row.getCell(ze_position + 2).getStringCellValue();
                ah_plan = row.getCell(ze_position + 3).getStringCellValue();
                ah_controlwork = row.getCell(ze_position + 4).getStringCellValue();

                ah_aud = row.getCell(ze_position + 5).getStringCellValue();

                ah_sr = row.getCell(ze_position + 6).getStringCellValue();

                ah_control = row.getCell(ze_position + 7).getStringCellValue();

                ah_preparation = row.getCell(ze_position + 8).getStringCellValue();
                for (int l = 0; l < semester_amount; l++) {
                    courses_semesters.add(row.getCell(ze_position + 9 + l).getStringCellValue());
                }

                faculty_code = row.getCell(ze_position + 9 + semester_amount).getStringCellValue();

                faculty_name = row.getCell(ze_position + 10 + semester_amount).getStringCellValue();

                countInPlan = (row.getCell(0).getStringCellValue().equals("+"));
                Plan p = new Plan(countInPlan, index, name, cf_exam, cf_midterm, cf_kp, cf_coursework, cf_controlwork, cf_midtermwithmark, ze_expert, ze_factual, ah_expert, ah_plan, ah_controlwork, ah_aud
                        , ah_sr
                        , ah_control
                        , ah_preparation
                        , courses_semesters
                        , faculty_code
                        , faculty_name);
                plans.add(p);
            }
            j++;
        }
        return plans;
    }

    public static void startParsing(String filename, String databaseURL, Properties dbProperties) throws Exception {
        Connection conn = getConnection(databaseURL,dbProperties);
        XSSFWorkbook workbook = readWorkbook(filename);
        Titular t = parseTitular(workbook);
        List<Competention> comps = parseCompetention(workbook);
        List<Plan> plans = parsePlan(workbook, t.program);

        PreparedStatement st = conn.prepareStatement("INSERT INTO public.titulars( profile, beginning_year, fgos) VALUES (?, ?, ?)");
        st.setString(1, t.profile);
        st.setLong(2, t.beginning_year);
        st.setString(3, t.fgos);
        st.executeUpdate();
        st.close();
        for (Competention comp :
                comps) {
            st = conn.prepareStatement("INSERT INTO COMPETENTIONS (sub_index, index, DESCRIPTION, type,titular) VALUES (?, ?, ?, ?, ?)");
            st.setString(1, comp.subIndex);
            st.setString(2, comp.index);
            st.setString(3, comp.description);
            st.setString(4, comp.type);
            st.setString(5, t.toString());
            st.executeUpdate();
            st.close();
        }
        for (Plan ps :
                plans) {
            st = conn.prepareStatement("INSERT INTO public.plans(" +
                    "count_in_plan, index, name, cf_exam, cf_midterm, cf_coursework, cf_controlwork, cf_midtermwithmark, cf_kp, " +
                    "ze_expert, ze_factual, ah_expert, ah_plan, ah_controlwork, ah_aud, ah_sr, ah_control, ah_preparation, " +
                    "faculty_code, faculty_name, titular, courses_semesters)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            st.setBoolean(1, ps.countInPlan);
            st.setString(2, ps.index);
            st.setString(3, ps.name);
            st.setString(4, ps.cf_exam);
            st.setString(5, ps.cf_midterm);
            st.setString(6, ps.cf_coursework);
            st.setString(7, ps.cf_controlwork);
            st.setString(8, ps.cf_midtermwithmark);
            st.setString(9, ps.cf_kp);
            st.setString(10, ps.ze_expert);
            st.setString(11, ps.ze_factual);
            st.setString(12, ps.ah_expert);
            st.setString(13, ps.ah_plan);
            st.setString(14, ps.ah_controlwork);
            st.setString(15, ps.ah_aud);
            st.setString(16, ps.ah_sr);
            st.setString(17, ps.ah_control);
            st.setString(18, ps.ah_preparation);
            st.setString(19, ps.faculty_code);
            st.setString(20, ps.faculty_name);
            st.setString(21, t.toString());
            st.setArray(22, conn.createArrayOf("varchar", new List[]{ps.courses_semesters}));
            st.executeUpdate();
            st.close();
        }
    }

    public static XSSFWorkbook readWorkbook(String filename) {
        try {
            return new XSSFWorkbook(new File(filename));
        } catch (Exception e) {
            return null;
        }
    }

    public static Connection getConnection(String databaseURL, Properties dbProperties) throws SQLException {
        return DriverManager.getConnection(databaseURL, dbProperties);
    }

}