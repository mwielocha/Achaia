package view

import _root_.util.DynamicTable
import scala.swing._
import _root_.model.{BrowseTableModel, BrowseModel}
import scala.collection.JavaConversions._
import javax.swing.border.{LineBorder, Border}
import javax.swing._
import java.awt.{ComponentOrientation, Color}
import javax.swing.table.{TableCellRenderer, TableColumn, TableColumnModel}
import java.{awt, util}
import javax.swing.event.TableColumnModelListener
import tools.ButtonColumn
import jsyntaxpane.DefaultSyntaxKit
import scala.collection.mutable.ArrayBuffer

/**
 * author mikwie
 *
 */
class BrowseView[K, C] extends BoxPanel(Orientation.Vertical) {

  private var browseModel: BrowseModel[K, C] = BrowseModel.empty

  val selections = ArrayBuffer[Publisher]()

  def rebuild = {

    peer.removeAll()
    selections.clear()

    browseModel.rows.toStream.take(50).foreach(row => {

      contents += new BorderPanel {
        add(new TextField(row.getKey.toString) {
          editable = false
          border = BorderFactory.createEmptyBorder()
          background = null
        }, BorderPanel.Position.North)
        val table = new DynamicTable() {
          peer.getSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        }
        table.model = new BrowseTableModel(row.getColumns)
        table.peer.getColumnModel.getColumn(0).setMaxWidth(400)
        selections += table.selection
        //table.adjustColumns
        add(new ScrollPane(table) {
          minimumSize = new Dimension(100, 100)
        },
          BorderPanel.Position.Center)
      }
    })

    revalidate()
    repaint()
  }

  def model_=(model: BrowseModel[K, C]) = {
    this.browseModel = model
    rebuild
  }

  def model = browseModel
}
