package Interface;



import RenderingEngine.Constructs.Mesh;
import RenderingEngine.CoreComponents.Vector4;
import RenderingEngine.Constructs.Vertex;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author E
 */
public class OBJModelLoader {
    private static final String FILENAME = ".+?(?=[.]obj$)";
    private static final String OBJEXTENSION = "[\\s\\S]+[.]obj$";
    private static final String NAME = "^o\\s[\\s\\S]+";
    private static final String VERTEX = "^v(\\s-?\\d+[.]\\d+){3,}";
    private static final String UVCOORDINATE= "^vt(\\s-?\\d+[.]\\d+){2,}";
    private static final String NORMAL = "^vn(\\s-?\\d+[.]\\d+){3,}";
    private static final String FACE = "^f(\\s\\d+([/]\\d*){2}){1,}";
    

    public static Mesh meshFromObjFile(File file) throws IOException {
        List<String> allLines = Files.readAllLines(file.toPath());
        OBJModel model = new OBJModelLoader().new OBJModel(allLines);
        return model.toIndexedModel().toMesh();
    }   
    private class OBJModel{
        private class OBJIndex{
            public int vertexIndex;
            public int UVcoordIndex;
            public int normalIndex;

            public int getVertexIndex() {return vertexIndex;}
            public int getUVcoordIndex() {return UVcoordIndex;}
            public int getNormalIndex() {return normalIndex;}
            public void setVertexIndex(int vertexIndex) {this.vertexIndex = vertexIndex;}
            public void setUVcoordIndex(int UVcoordIndex) {this.UVcoordIndex = UVcoordIndex;}
            public void setNormalIndex(int normalIndex) {this.normalIndex = normalIndex;}
            @Override
            public boolean equals(Object obj){
                OBJIndex index = (OBJIndex)obj;

                return 
                    vertexIndex == index.vertexIndex
                    && UVcoordIndex == index.UVcoordIndex
                    && normalIndex == index.normalIndex;
            }

            @Override
            public int hashCode(){
                final int BASE = 17;
                final int MULTIPLIER = 31;

                int result = BASE;

                result = MULTIPLIER * result + vertexIndex;
                result = MULTIPLIER * result + UVcoordIndex;
                result = MULTIPLIER * result + normalIndex;

                return result;
            }
        }
        private class IndexedModel {
            private List<Vector4> positions;
            private List<Vector4> UVcoords;
            private List<Vector4> normals;
            private List<Integer> indices;

            public List<Vector4> getPositions() {return positions;}
            public List<Vector4> getUVcoords() {return UVcoords;}
            public List<Vector4> getNormals() {return normals;}
            public List<Integer> getIndices() {return indices;}

            public IndexedModel() {
                positions = new ArrayList<Vector4>();
                UVcoords = new ArrayList<Vector4>();
                normals = new ArrayList<Vector4>();
                indices = new ArrayList<Integer>();
            }

            public void calcNormals(){
                for(int i = 0; i < indices.size(); i += 3){
                    int i0 = indices.get(i);
                    int i1 = indices.get(i + 1);
                    int i2 = indices.get(i + 2);

                    Vector4 v1 = positions.get(i1).sub(positions.get(i0));
                    Vector4 v2 = positions.get(i2).sub(positions.get(i0));

                    Vector4 normal = v1.cross(v2).normalized();

                    normals.set(i0, normals.get(i0).add(normal));
                    normals.set(i1, normals.get(i1).add(normal));
                    normals.set(i2, normals.get(i2).add(normal));
                }
                for(int i = 0; i < normals.size(); i++)
                    normals.set(i, normals.get(i).normalized());
            }

            public Mesh toMesh(){
                List<Vertex> vertices = new ArrayList<>();
                for(int i = 0; i < positions.size(); i++){
                    vertices.add(new Vertex(
                        positions.get(i),
                        UVcoords.get(i),
                        normals.get(i)
                    ));
                }
                return new Mesh(vertices, indices);
            }
        }
        private List<Vector4> positions;
        private List<Vector4> UVcoords;
        private List<Vector4> normals;
        private List<OBJIndex> indices;
        private boolean        hasUVcoords;
        private boolean        hasNormals;
        public OBJModel(List<String> allLines){
            positions = new ArrayList<>();
            UVcoords = new ArrayList<>();
            normals = new ArrayList<>();
            indices = new ArrayList<>();
            hasUVcoords = false;
            hasNormals = false;

            for(String line : allLines){
                line = line.trim();
                String[] parts = line.split("\\s");
                if(line.matches(VERTEX)){
                    positions.add(new Vector4(
                            Float.valueOf(parts[1]),
                            Float.valueOf(parts[2]),
                            Float.valueOf(parts[3]), 1));
                }    
                else if(line.matches(UVCOORDINATE)){
                    UVcoords.add(new Vector4(
                            Float.valueOf(parts[1]),
                            1f - Float.valueOf(parts[2]), 0, 0));
                }   
                else if(line.matches(NORMAL)){
                    normals.add(new Vector4(
                            Float.valueOf(parts[1]),
                            Float.valueOf(parts[2]),
                            Float.valueOf(parts[3]), 0));
                }    
                else if(line.matches(FACE)){
                    for(int i = 0; i < parts.length - 3; i++){
                        indices.add(parseOBJIndex(parts[1]));
                        indices.add(parseOBJIndex(parts[2 + i]));
                        indices.add(parseOBJIndex(parts[3 + i]));
                    }
                }
            }
        }
        public OBJIndex parseOBJIndex(String part){
            String[] values = part.split("/");
            OBJIndex result = new OBJIndex();
            result.setVertexIndex(Integer.parseInt(values[0]) - 1);
            if(values.length > 1){
                if(!values[1].isEmpty()){
                    hasUVcoords = true;
                    result.setUVcoordIndex(Integer.parseInt(values[1]) - 1);
                }
                if(values.length > 2){
                    hasNormals = true;
                    result.setNormalIndex(Integer.parseInt(values[2]) - 1);
                }
            }
            return result;
        }
        public IndexedModel toIndexedModel(){
            IndexedModel result = new IndexedModel();
            IndexedModel normalModel = new IndexedModel();
            Map<OBJIndex, Integer> resultIndexMap = new HashMap<OBJIndex, Integer>();
            Map<Integer, Integer> normalIndexMap = new HashMap<Integer, Integer>();
            Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();

            for(int i = 0; i < indices.size(); i++){
                    OBJIndex currentIndex = indices.get(i);
                    Vector4 currentPosition = positions.get(currentIndex.getVertexIndex());
                    Vector4 currentUVcoord;
                    Vector4 currentNormal;

                    if(hasUVcoords)
                        currentUVcoord = UVcoords.get(currentIndex.getUVcoordIndex());
                    else
                        currentUVcoord = new Vector4(0,0,0,0);
                    if(hasNormals)
                        currentNormal = normals.get(currentIndex.getNormalIndex());
                    else
                        currentNormal = new Vector4(0,0,0,0);

                    Integer modelVertexIndex = resultIndexMap.get(currentIndex);
                    if(modelVertexIndex == null){
                        modelVertexIndex = result.getPositions().size();
                        resultIndexMap.put(currentIndex, modelVertexIndex);

                        result.getPositions().add(currentPosition);
                        result.getUVcoords().add(currentUVcoord);
                        if(hasNormals)
                            result.getNormals().add(currentNormal);
                    }

                    Integer normalModelIndex = normalIndexMap.get(currentIndex.getVertexIndex());
                    if(normalModelIndex == null){
                        normalModelIndex = normalModel.getPositions().size();
                        normalIndexMap.put(currentIndex.getVertexIndex(), normalModelIndex);
                        normalModel.getPositions().add(currentPosition);
                        normalModel.getUVcoords().add(currentUVcoord);
                        normalModel.getNormals().add(currentNormal);
                    }

                    result.getIndices().add(modelVertexIndex);
                    normalModel.getIndices().add(normalModelIndex);
                    indexMap.put(modelVertexIndex, normalModelIndex);
            }
            if(!hasNormals){
                normalModel.calcNormals();
                for(int i = 0; i < result.getPositions().size(); i++)
                    result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
            }
            return result;
        }
    }
}
